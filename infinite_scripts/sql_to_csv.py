#!/usr/bin/python

# Pulls stock information out of the SQL DB, writes it
# to a CSV file called 'something.csv'.

# Change the following line to get a certain ticker as
# the first column.
firstCol = 'USO'

import os
import datetime
f = open('query.sql','w')
today = datetime.datetime.now().strftime("%Y-%m-%d")
ayearago = (datetime.datetime.now() - datetime.timedelta(days=365)).strftime("%Y-%m-%d")
line = 'SELECT ticker,price FROM augurworks.stocks WHERE DATE(date) BETWEEN "' + ayearago + '" AND "' + today + '" ORDER BY date DESC;'
f.write(line)
f.close()

stream = os.popen('mysql -uroot -paugurworks < query.sql && rm query.sql')

stocks = dict()

stream.readline()

count = 0
for line in stream:
    count = count + 1
    words = line.split()
    if words[0] in stocks:
        stocks[words[0]].append(words[1])
    else:
        stocks[words[0]] = [words[1]]

keys = stocks.keys()

# Send 'firstCol' to the front of keys
if firstCol in keys:
    keys.insert(0,keys.pop(keys.index(firstCol)))
else:
    print firstCol + " not found in DB. First column will be " + keys[0] + "."

cutoff = count / len(keys)
f = open('something.csv','w')
lcv = 0
while lcv < cutoff:
    line = ''
    for key in keys:
        line = line + str(stocks[key][lcv]) + ','
    line = line[:-1]
    line = line + "\n"
    f.write(line)
    lcv = lcv + 1

f.close()
