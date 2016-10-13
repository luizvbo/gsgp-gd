# ============= Gera as médias e DPs dos MSEs para uma pasta com vários resultados ================
getFolderRMSEs = function(path, pattern = NULL, last.iteration=NA, report.median = F, n.executions = NA){
  pastas=dir(path, full.names = T, pattern = pattern, recursive=F)
  n = last.iteration
  for(i in pastas){
    getRMSE(i, last.iteration, report.median, n.executions)
  }
}

# Given a path to a folder with GSGP output print on the screen the mean/SD or median/IQR 
getRMSE = function(path, last.iteration=NA, report.median = F, n.executions = NA){
  #tr=read.table(paste(path,"/trRMSEperIteration.csv",sep=""), sep=",", header=F, skip=1)
  tr=read.table(paste(path,"/trFitness.csv",sep=""), sep=",", header=F)
  #ts=read.table(paste(path,"/tsRMSEperIteration.csv",sep=""), sep=",", header=F, skip=1)
  ts=read.table(paste(path,"/tsFitness.csv",sep=""), sep=",", header=F)
  if(is.na(last.iteration)){
    n = dim(tr)[2]
  }
  if(is.na(n.executions)){
    n.executions = dim(tr)[1]
  }
  if(report.median)
    cat(path,",",median(tr[1:n.executions,n]),",",IQR(tr[1:n.executions,n]),",",
        median(ts[1:n.executions,n]),",",IQR(ts[1:n.executions,n]),"\n", sep="")
  else
    cat(path,",",mean(tr[1:n.executions,n]),",",sd(tr[1:n.executions,n]),",",
        mean(ts[1:n.executions,n]),",",sd(ts[1:n.executions,n]),"\n", sep="")
}
