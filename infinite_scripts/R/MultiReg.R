#This file takes in a formatted training file and creates an array of 1 day predictions

#Opens the file and reads it into the close variable
close <- read.csv("D:\\Users\\TheConnMan\\git\\Core\\infinite_scripts\\R\\output.csv",header=TRUE)

#Number of days each regression is trained on
trainNum <- 30
#Number of days predicted. Each is predicted from the 'trainNum' days previous
count <- 1200#dim(close)-trainNum-1
#Create an empty results array
csv <- rep(0,count)
#For each day train on the previous 'tranNum' days and predict on the current day
for (start in 0:count) {
#Create the training file
train <- subset(close, y>(1+start) & y<(trainNum+1+start))
#Create the prediction file
pred <- subset(close, y<=(1+start) & y>start)
#Create the fit based on the data columns
fit <- lm(Output ~ USO0 + DJIA0 + UNG0 + USO1 + DJIA1 + UNG1 + USO2 + DJIA2 + UNG2 + USO3 + DJIA3 + UNG3 + USO4 + DJIA4 + UNG4, data=train)
#Do the prediction
result <- predict.lm(fit, pred)
#Add the result to the results array
csv[start] = result
}
#Write the results to a file
write.csv(csv, file = "D:\\Users\\TheConnMan\\git\\Core\\infinite_scripts\\R\\Multi.csv")