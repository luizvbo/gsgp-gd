#' Check if the median of the output data in p1 is greater than the data in p2
#' 
#' @param p1 Path of the first data.
#' @param p2 Path of the second data.
#' @param sort.1 True if we need to sort the data from p1
#' @param sort.2 True if we need to sort the data from p2
#' @paired paired True if the data is paired
#' @paired test 'wilcoxon' or 'ttest'
#' @paired type What kind of data to examine
#' @return The test p-value (positive if there p1 > p2 and negative otherwise)
statTest = function(p1, p2, sort.1 = T, sort.2 = T, paired = T, test = 'wilcoxon', type='test'){
  # csvFileName = 'tsFitness.csv'
  csvFileName = switch (type,
    test = 'tsFitness.csv',
    training = 'trFitness.csv',
    size = 'numNodes.csv',
    time = 'elapsedTime.csv',
    stop("Type not found. The available types are: test, training, size.")
  )
  aux = paste(p1, "/", csvFileName, sep = "")
  d1 = read.csv(paste(p1, "/", csvFileName, sep = ""), header = F)
  aux = paste(p2, "/", csvFileName, sep = "")
  d2 = read.csv(paste(p2, "/", csvFileName, sep = ""), header = F)
  if(sort.1) d1 = d1[order(d1[,1]),]
  if(sort.2) d2 = d2[order(d2[,1]),]
  n.c1 = dim(d1)[2]
  n.c2 = dim(d2)[2]
  # Test the hypothesis that the config.1 < config.2
  if(test == 'wilcoxon'){
    p.value.lt = wilcox.test(d1[,n.c1], d2[,n.c2], paired = paired, alternative = 'l')$p.value
    # Test the hypothesis that the config.1 > config.2
    p.value.gt = wilcox.test(d1[,n.c1], d2[,n.c2], paired = paired, alternative = 'g')$p.value
  }
  else{
    p.value.lt = t.test(d1[,n.c1], d2[,n.c2], paired = paired, alternative = 'l')$p.value
    # Test the hypothesis that the config.1 > config.2
    p.value.gt = t.test(d1[,n.c1], d2[,n.c2], paired = paired, alternative = 'g')$p.value
  }
  if(p.value.lt < p.value.gt) p.value = p.value.lt
  else p.value = -p.value.gt
  cat(basename(p1), ",", basename(p2), ",", p.value, "\n", sep="") 
}

# This function is very specific. It list two folders and, following some guidelines, compare the similar 
# folders (with GSGP results) in which it runs a Wilcoxon test
statTestBetweenFolders = function(p1, p2, sort.1 = T, sort.2 = T, 
                                 paired = T, ds.names, test='wilcoxon', type='test'){
  # Get the dataset folders
  p1.list=list.dirs(p1, recursive = F, full.names = T)
  p2.list=list.dirs(p2, recursive = F, full.names = T)
  for(ds in ds.names){
    p1.ds.path = grep(ds, p1.list, value = T)
    p2.ds.path = grep(ds, p2.list, value = T)
    # Test if the dataset is present in both paths
    if(length(p1.ds.path)!=0 & length(p2.ds.path)!=0){
      statTest(p1.ds.path[1], p2.ds.path[1], sort.1 = sort.1, 
              sort.2 = sort.2, paired = paired, test = test, type = type)
    }
  } 
}
