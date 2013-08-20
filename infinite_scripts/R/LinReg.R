close <- read.csv("C:\\Dropbox\\AugurWorks\\R\\output.csv",header=TRUE)
start <- 102
cut <- 1
trainNum <- 30
train <- subset(close, y>(cut+start) & y<(trainNum+cut+start))
pred <- subset(close, y<=(cut+start) & y>start)
fit <- lm(Output ~ USO0 + DJIA0 + UNG0 + USO1 + DJIA1 + UNG1 + USO2 + DJIA2 + UNG2 + USO3 + DJIA3 + UNG3 + USO4 + DJIA4 + UNG4, data=train)
summary(fit)
result <- predict.lm(fit, pred)
output <- cbind(result, pred[2])
plot(cbind(c(cut:1),result), type="o", col="blue")
lines(cbind(c(cut:1),pred[2]), type="o", col="red")
lines(cbind(c(cut:1),rep(0,cut)), type="l", col="black")