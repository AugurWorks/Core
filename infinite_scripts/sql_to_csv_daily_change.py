def previousYear(year, month):
    if month==0:
        return year-1
    else:
        return year

def previousMonth(month):
    return (month+11)%12

def currentCPI(cpi, daysInMonth, year, month, day):
    done=False
    withinCPI=True
    while not done:
        if not cpi[year][month]==0:
            done=True
        else:
            year=previousYear(year, month)
            month=previousMonth(month)
            day+=daysInMonth[month]
            withinCPI=False
    lastYear=previousYear(year, month)
    lastMonth=previousMonth(month)
    if withinCPI:
        return cpi[lastYear][lastMonth]+day/daysInMonth[month]*(cpi[year][month]-cpi[lastYear][lastMonth])
    else:
        return cpi[year][month]+day/30*(cpi[year][month]-cpi[lastYear][lastMonth])

#!/usr/bin/python

# Pulls stock information out of the SQL DB, writes it
# to a CSV file called 'something.csv'.

# Change the following line to get a certain ticker as
# the first column.
firstCol = 'USO'

import os
import datetime
import csv

readFileName='CPI.csv'
fileReader=open(readFileName,'rb')
reader=csv.reader(fileReader)
cpi = [[0]*12 for x in range(50)]
rowNum=-1
for row in reader:
    if rowNum==-1:
        rowNum=0
        continue
    for i in range(12):
        cpi[rowNum][i]=float(row[i+1])
    rowNum+=1

fileReader.close()

f = open('/root/Core/infinite_scripts/query.sql','w')
today = datetime.datetime.now().strftime("%Y-%m-%d")
ayearago = "2008-01-01"
daysInMonth=[31,28,31,30,31,30,31,31,30,31,30,31]
line = 'SELECT ticker,price,date,day_change FROM augurworks.stocks WHERE (DATE(date) BETWEEN "' + ayearago + '" AND "' + today + '") ORDER BY date DESC;'
f.write(line)
f.close()

stream = os.popen('mysql -uroot -paugurworks < /root/Core/infinite_scripts/query.sql && cat /root/Core/infinite_scripts/query.sql')

stocks = dict()
dates = dict()
dayChanges = dict()
dateList = []

stream.readline()

f2 = open('/root/Core/infinite_scripts/dates.csv','w')
count = 0
for line in stream:
    if "SELECT" in line:
        continue
    count = count + 1
    words = line.split()
    if words[0] in stocks.keys():
        stocks[words[0]].append(words[1])
        dates[words[0]].append(words[2])
        dayChanges[words[0]].append(words[3])
    else:
        stocks[words[0]] = [words[1]]
        dates[words[0]] = [words[2]]
        dayChanges[words[0]] = [words[3]]
    if words[2] not in dateList:
        dateList.append(words[2])

dateList.sort()
dateList.reverse()
for i in range(len(dateList)):
    f2.write(dateList[i]+"\n")
    
keys = stocks.keys()

# Send 'firstCol' to the front of keys
if firstCol in keys:
    keys.insert(0,keys.pop(keys.index(firstCol)))
else:
    print firstCol + " not found in DB. First column will be " + keys[0] + "."

cutoff = count / len(keys)
f = open('/root/Core/infinite_scripts/day_changes.csv','w')
lcv = 0

curDate=dates[firstCol][lcv]
year=int(today[0:4])-1970
month=int(today[5:7])-1
day=int(curDate[8:10])

todayCPI=currentCPI(cpi, daysInMonth, year, month, day)

while lcv < cutoff:
    curDate=dates[firstCol][lcv]
    year=int(curDate[0:4])-1970
    month=int(curDate[5:7])-1
    day=int(curDate[8:10])
    curCPI=currentCPI(cpi, daysInMonth, year, month, day)
    line = ''
    line = line + dayChanges[keys[0]][lcv] + ','
    for key in keys:
        line = line + str(float(stocks[key][lcv])/curCPI*todayCPI) + ','
    line = line[:-1]
    line = line + "\n"
    f.write(line)
    lcv = lcv + 1

f.close()
f2.close()
