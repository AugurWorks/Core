import csv

daysBack = 5
daysAhead = 1
includedStocks = ['USO', 'DJIA', 'S.P.500', 'UNG']
predictor = 'USO'

data = open('daychange.csv', 'rb')
reader = csv.reader(data)
out = open('output.csv', 'wb')

rowNum = 0
header = []
dataArray = []
predictorIndex = -1
for row in reader:
    if rowNum == 0:
        predictorIndex = row.index(predictor)
        line = 'y,Output'
        line2 = 'fit <- lm(Output' + ' ~ '
        for col in row:
            if col in includedStocks:
                header.append(row.index(col))
        for i in range(daysBack):
            for col in header:
                line += ',' + str(row[col]) + str(i)
                line2 += str(row[col]) + str(i) + ' + '
        print line2[0:-3] + ', data=train)'
        out.write(line + '\n')
    else:
        array = []
        for col in header:
            array.append(row[col])
        dataArray.append(array)
    rowNum += 1

for i in range(len(dataArray) - daysBack - daysAhead):
    day = i + daysAhead
    line = str(i + 1) + ',' + str(dataArray[day + daysAhead][predictorIndex])
    for back in range(daysBack):
        for col in range(len(header)):
            line += ',' + dataArray[day - back][col]
    out.write(line + '\n')
    rowNum += 1

data.close()
out.close()
