#' Plot the the mean and median training and test RMSE.
#' 
#' @param path Path to the folder with the results.
#' @param n Index of the last generation. NA (default) to use the last generation.
#' @param plot.on.screen If TRUE (default) draw the plots on the screen.
#' @param outputPath If plot.on.screen is FALSE, write the plots on a file in the \code{outputPath}.
convergencePlot = function(path, n=NA, plot.on.screen=T, outputPath = path){
  tr=read.table(paste(path,"/trFitness.csv", sep=""), sep=",", header=F)
  ts=read.table(paste(path,"/tsFitness.csv", sep=""), sep=",", header=F)
  
  config = strsplit(path, "/")
  config = config[[1]]
  config = config[length(config)]
  if(!plot.on.screen) pdf(paste(outputPath, "/RMSE_", config, ".pdf", sep=""), width=7, height=5)
  else par(mfrow=c(1,2))
  if(!is.na(n)){
    tr = tr[,1:n]
    ts = ts[,1:n]
  }
  tr.median=apply(tr[2:dim(tr)[2]],2,median)
  ts.median=apply(ts[2:dim(ts)[2]],2,median)
  tr.mean=apply(tr[2:dim(tr)[2]],2,mean)
  ts.mean=apply(ts[2:dim(ts)[2]],2,mean)
  plot(tr.median, type='l', ylab="Training RMSE", xlab="generation", lwd=1, col='blue'); 
  legend("topright", c("Training","Test"), lty=c(1,1), lwd=c(1,1), col=c('blue', 'red'),  bty = "n")
  title(main="Median")
  par(new=T)
  plot(ts.median, type='l', ylab=NA, xlab=NA, lty=1, lwd=1, axes=F, col='red')
  axis(4)
  
  plot(tr.mean, type='l', ylab="Training RMSE", xlab="iteration", lwd=1, col='blue'); 
  legend("topright", c("Training","Test"), lty=c(1,1), lwd=c(1,1), col=c('blue', 'red'), bty = "n")
  title(main="Mean")
  par(new=T)
  plot(ts.mean, type='l', ylab=NA, xlab=NA, lty=1, lwd=1,  axes=F, col='red')
  axis(4)
  if(!plot.on.screen) dev.off()
  else par(mfrow=c(1,1))
}