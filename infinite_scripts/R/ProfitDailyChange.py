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
brokerFee=4
minDiff=0.2
daysAhead=1

print "Starting conditions: Start with "+str(stock)+" stock and $"+str(cash)+"."

dayBought=0
daySold=0
writer.write('Day,Actual,Prediction,Bought,Sold,Profit\n')

pp=0
pm=0
mp=0
mm=0
dif=0

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


    if actualTemp>0 and predictedChange[yesterday]>minDiff:
        pp+=1
    elif actualTemp<0 and predictedChange[yesterday]>minDiff:
        mp+=1
    elif actualTemp>0 and predictedChange[yesterday]<-minDiff:
        pm+=1
    elif actualTemp<0 and predictedChange[yesterday]<-minDiff:
        mm+=1
    else:
        dif+=1
    
    if predTemp>minDiff and stock==0:
        stock=(cash-brokerFee)/openTemp
        print "Stock bought: "+str((cash-brokerFee)/openTemp)+" shares at $"+str(openTemp)+" and $"+str(cash)+" spent. Days with cash="+str(i-daySold)+". Day number="+str(i)
        cash=0
        dayBought=i
        string+=str(actualTemp)+','

    elif predTemp<-minDiff and cash==0:
        cash=stock*openTemp-brokerFee
        cashEquiv=cash
        print "Stock sold: "+str(stock)+" shares at $"+str(openTemp)+" for $"+str(cash)+". Days with stock="+str(i-dayBought)+". Day number="+str(i)
        stock=0
        daySold=i
        string+=','+str(actualTemp)

    else:
        string+=','

    string+=','+str(cashEquiv/initialCash*100-100)+'\n'

    writer.write(string)

print "pp="+str(pp)+", pm="+str(pm)+", mp="+str(mp)+", mm="+str(mm)+", dif="+str(dif)
tot=pp+pm+mp+mm
print "Percent good="+str(float(pp+mm)/float(tot)*100)+", percent bad="+str(float(pm+mp)/float(tot)*100)

print "Final results:"
print "Stock held at end="+str(stock)+" shares at a current price of $"+str(close[0])+"/share worth $"+str(stock*close[0])+"."
print "Cash held=$"+str(cash)+"."
print "Total value=$"+str(cash+stock*close[0])+"."
print "Cash equivalent="+str(cashEquiv)+"."
print "Our increase="+str((cash+stock*close[0]-initialCash)/initialCash*100)+"%."
print "Our cash equivalent increase="+str(cashEquiv/initialCash*100-100)+"%."

writer.close()
