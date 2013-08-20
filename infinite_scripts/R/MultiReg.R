close <- read.csv("C:\\Dropbox\\AugurWorks\\R\\output.csv",header=TRUE)

trainNum <- 30
count <- 1200#dim(close)-trainNum-1
csv <- rep(0,count)
for (start in 0:count) {
train <- subset(close, y>(1+start) & y<(trainNum+1+start))
pred <- subset(close, y<=(1+start) & y>start)
fit <- lm(Output ~ USO0 + DJIA0 + UNG0 + USO1 + DJIA1 + UNG1 + USO2 + DJIA2 + UNG2 + USO3 + DJIA3 + UNG3 + USO4 + DJIA4 + UNG4, data=train)
result <- predict.lm(fit, pred)
output <- cbind(result, pred[2])
csv[start] = result
}
write.csv(csv, file = "C:\\Dropbox\\AugurWorks\\R\\Multi.csv")