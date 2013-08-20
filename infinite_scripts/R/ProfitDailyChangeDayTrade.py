import csv

readFileName='Multi2.csv'
fileReader=open(readFileName,'rb')
writer=open('MultiOutput.csv','wb')
reader=csv.reader(fileReader)

realChange=[]
predictedChange=[]
openPrice=[]
close=[]
rowNum = 1
for row in reader:
    if not rowNum == 1:
        realChange.append(float(row[0]))
        predictedChange.append(float(row[1]))
        openPrice.append(float(row[2]))
        close.append(float(row[3]))
    rowNum+=1
    
fileReader.close()

length=len(realChange)
cash=5000
initialCash=cash
cashEquiv=cash
stock=0
brokerFee=0
minDiff=.1
daysAhead=1

print "Starting conditions: Start with "+str(stock)+" stock and $"+str(cash)+"."

dayBought=0
daySold=0
writer.write('Day,Actual,Prediction,Bought,Sold,Profit\n')

for i in range(4,length-2):
    tomorrow=length-i-2
    today=length-i-1
    yesterday=length-i
    twoDaysAgo=length-i+1


    predTemp=predictedChange[today]
    actualTemp=realChange[yesterday]
    realTemp=close[yesterday]
    openTemp=openPrice[today]
    string=str(i)+','+str(actualTemp)+','+str(predictedChange[yesterday])+','

    
    if predTemp<-minDiff:
        cash=(cash-brokerFee)/openTemp*close[today]-brokerFee
        print "Bought at $"+str(openTemp)+". Sold at $"+str(close[today])+". Cash=$"+str(cash)+"."
        string+=str(close[today]-openTemp)+','

##    elif predTemp>minDiff:
##        cash=(cash-brokerFee)/close[today]*openTemp-brokerFee
##        print "Sold at $"+str(openTemp)+". Bought at $"+str(close[today])+". Cash=$"+str(cash)+"."
##        string+=','+str(close[today]-openTemp)
    else:
        string+=','

    string+=','+str(cash/initialCash*100-100)+'\n'

    writer.write(string)

print "Final results:"
print "Stock held at end="+str(stock)+" shares at a current price of $"+str(close[0])+"/share worth $"+str(stock*close[0])+"."
print "Cash held=$"+str(cash)+"."
print "Total value=$"+str(cash+stock*close[0])+"."
print "Cash equivalent="+str(cashEquiv)+"."
print "Our increase="+str((cash+stock*close[0]-initialCash)/initialCash*100)+"%."

writer.close()
