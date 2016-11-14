# O algoritmo de controle é sempre aquele que se encontra na segunda coluna 
# (a primeira coluna contem o nome dos datasets)
friedman.with.Finner = function(data, alpha=0.05){
  k=dim(data)[2]-1
  n=dim(data)[1]
  avg.rank=apply(apply(data[,2:dim(data)[2]], 1,rank),1,mean)
  summation=sum(avg.rank^2)
  Xf=(12*n)/(k*(k+1))
  # Xf é a estatística de Friedman, distribuída segundo uma chi square com k-1 df
  Xf=Xf*(summation-((k*(k+1)^2)/4))
  Xf.pvalue=1-pchisq(Xf, k-1)
  #Ff é Xf corrigido - Iman and Davenport (1980)
  Ff=((n-1)*Xf)/(n*(k-1)-Xf)
  # Ff é distribuída segundo uma distribuição F
  Ff.pvalue=1-pf(Ff, k-1, (k-1)*(n-1))
  stat=list(Xf=Xf, Xf.pvalue=Xf.pvalue, Ff=Ff, Ff.pvalue=Ff.pvalue)
  # Z value for the Friedman test
  z=(avg.rank[1]-avg.rank)/sqrt((k*(k+1))/(6*n))
  z=z[2:k]
  # P-values for the Friedman test
  pvalue=2*pnorm(-abs(z), mean = 0, sd=1)
  pvalue.ord=sort(pvalue, index.return = T, decreasing = F)$ix
  v.aux=1-(1-pvalue[pvalue.ord])^((k-1)/(1:(k-1)))
  pvalue.finner=c()
  for(i in 1:(k-1)){
    pvalue.finner=append(pvalue.finner, min(max(v.aux[1:i]),1))
  }
  names(pvalue.finner)=names(data)[3:(k+1)][pvalue.ord]
  stat=list(Xf=Xf, Xf.pvalue=Xf.pvalue, Ff=Ff, Ff.pvalue=Ff.pvalue, Finner.pvalue=pvalue.finner)
  #alpha.finner=1-(1-alpha)^((k-1)/(1:k))
  return(stat)
}

# ====== Função para gerar uma matrix compatível com o teste de Friedman com post-hoc de Nemenyi =========
friedman.with.nemenyi = function(originalData){
  # Carrega o pacote PMCMR
  library("PMCMR")

  # Os dados devem estar em colunas dataset, alg1, alg2, ..., algn
  # Os nomes das colunas da matriz deve ser o nome do algoritmo
  size = dim(originalData)
  
  # Groups = different algorithms
  # Blocks = different datasets
  newData <- data.frame(matrix(vector(), size[1]*(size[2]-1), 3, dimnames=list(c(), 
                        c("groups","blocks","values"))), 
                        stringsAsFactors=F) 
  
  algNames = colnames(originalData)[2:size[2]]
  aux=NULL
  for(i in 1:length(algNames)) aux = c(aux,rep(algNames[i], size[1]))
  newData[,1]=aux
  newData[,2]=rep(as.character(originalData[,1]),size[2]-1)
  aux=NULL
  for(i in 2:size[2]) aux = c(aux,originalData[,i])
  newData[,3]=as.numeric(aux)
  print(posthoc.friedman.nemenyi.test(y=newData$values, 
  						groups = newData$groups, blocks = newData$blocks), p.value=0.5)
}
