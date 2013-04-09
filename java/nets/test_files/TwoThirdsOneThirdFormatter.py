import csv, math

#Calculate the mean of a part of an array starting at start and of length length
def mean(array,start,length):
    num=0
    for i in range(start,start+length):
        num+=array[i]
    return num

#Calculate the variance of a part of an array starting at start and of length length
def var(array,start,length):
    meanNum=mean(array,start,length)
    var=0
    for i in range(start,start+length):
        var+=math.pow(array[i]-meanNum,2)
    return var

#Scale
def scale(value,mx,mn,maxNum,minNum):
    return (value-mn)/(mx-mn)*(maxNum-minNum)+minNum

readFileName='Generic Full Training Data.csv'
writer2=open('OneThird.augtrain', 'wb') #Open testing file for writing
writer=open('TwoThirds.augtrain', 'wb') #Open training file for writing
fileReader=open(readFileName,'rb')
reader=csv.reader(fileReader) #Open and read file at readFileName

#Header data for .augtain files
iterationsPerRow=1
iterationsOfFile=5000
learningConstant=.001
minNumTrainingRounds=5000
cutoffOfPerformance=.001
arrayDepth=4

#Information for creating the .augtrain files
daysBack=8 #Number of additional days used as inputs for stock data
varDays=200 #Number of days used in calculating the recent variance
daysAhead=1 #Number of days ahead predicted

#Count how many total days are in the file
numRows=0
#Count how many different stocks are used
numStocks=0
for row in reader:
    if numRows==0:
        numStocks=len(row)
    numRows+=1
fileReader.close()

#Instantiate arrays of size [numStocks][rowNum]
close=[[0 for i in range(numRows)] for j in range(numStocks)]
percentageChange=[[0 for i in range(numRows)] for j in range(numStocks)]
variance=[[0 for i in range(numRows)] for j in range(numStocks)]

#Copy .csv to close array
fileReader=open(readFileName,'rb')
reader=csv.reader(fileReader)
rowNum=0
for row in reader:
    for i in range(len(row)):
        close[i][rowNum]=float(row[i])
    rowNum+=1
fileReader.close()

#Calculate change, percentageChange, and variance for each day and input
for i in range(numStocks):
    for j in range(numRows-1):
        percentageChange[i][j]=(close[i][j]-close[i][j+1])/close[i][j+1]*100
    for j in range(numRows-varDays):
        variance[i][j]=var(close[i],j,varDays)

#Define max, min, and max/min arrays for scaling
maxNum=.9
minNum=.1
maxArray=[[float("-inf") for i in range(numStocks)] for j in range(3)]
minArray=[[float("inf") for i in range(numStocks)] for j in range(3)]

#Find max and mins of all inputs
for i in range(numStocks):
    for j in range(numRows-varDays):
        maxArray[0][i]=max(maxArray[0][i],close[i][j])
        maxArray[1][i]=max(maxArray[1][i],percentageChange[i][j])
        maxArray[2][i]=max(maxArray[2][i],variance[i][j])
        minArray[0][i]=min(minArray[0][i],close[i][j])
        minArray[1][i]=min(minArray[1][i],percentageChange[i][j])
        minArray[2][i]=min(minArray[2][i],variance[i][j])

#Write header rows for .augtrain file
writer.write('net '+str(numStocks*daysBack*3)+','+str(arrayDepth)+'\n')
writer2.write('net '+str(numStocks*daysBack*3)+','+str(arrayDepth)+','+str(maxArray[0][0])+','+str(minArray[0][0])+','+str(maxNum)+','+str(minNum)+'\n')
writer.write('train '+str(iterationsPerRow)+','+str(iterationsOfFile)+','+str(learningConstant)+','+str(minNumTrainingRounds)+','+str(cutoffOfPerformance)+'\n')
writer2.write('train '+str(iterationsPerRow)+','+str(iterationsOfFile)+','+str(learningConstant)+','+str(minNumTrainingRounds)+','+str(cutoffOfPerformance)+'\n')
string='TITLES '
for j in range(daysBack):
    for i in range(numStocks):
        string+='Close_Day_Back_'+str(j)+',Percent_Change_Day_Back_'+str(j)+',Variance_Day_Back_'+str(j)
        if not (i==numStocks-1 and j==daysBack-1):
            string+=','
writer.write(string+'\n')
writer2.write(string+'\n')

oneThirdMark=(numRows-varDays-daysBack-daysAhead)/3
#Write the rest of the .augtrain files
for r in range(daysAhead,numRows-varDays-daysBack):
    string=str(scale(close[0][r-daysAhead],maxArray[0][0],minArray[0][0],maxNum,minNum))+' '
    for j in range(daysBack):
        for i in range(numStocks):
            string+=str(scale(close[i][r+j],maxArray[0][i],minArray[0][i],maxNum,minNum))+','+str(scale(percentageChange[i][r+j],maxArray[1][i],minArray[1][i],maxNum,minNum))+','+str(scale(variance[i][r+j],maxArray[2][i],minArray[2][i],maxNum,minNum))
            if not (i==numStocks-1 and j==daysBack-1):
                string+=','
    if r+daysAhead<oneThirdMark:
        writer2.write(string+'\n')
    else:
        writer.write(string+'\n')
    
#Close writers
writer.close()
writer2.close()

