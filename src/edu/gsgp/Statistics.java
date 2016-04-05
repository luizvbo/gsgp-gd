/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp;

import edu.gsgp.data.ExperimentalData;
import edu.gsgp.population.GSGPIndividual;
import edu.gsgp.population.Population;
import edu.gsgp.population.Individual;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Statistics {
    
    public enum StatsType{
        BEST_OF_GEN_SIZE("individualSize.csv"), 
        SEMANTICS("outputs.csv"),
        BEST_OF_GEN_TS_FIT("tsFitness.csv"), 
        BEST_OF_GEN_TR_FIT("trFitness.csv"),
        ELAPSED_TIME("elapsedTime.csv"),
        LOADED_PARAMETERS("loadedParams.txt"),
        MDD_AVG("mddAverage.csv"),
        MDD_SD("mddStdDev.csv");
        
        private final String filePath;

        private StatsType(String filePath) {
            this.filePath = filePath;
        }
        
        public String getPath(){
            return filePath;
        }
    }
    
    protected ExperimentalData expData;
    
    protected float elapsedTime;
    protected String[] bestOfGenSize;
    protected String[] bestOfGenTsFitness;
    protected String[] bestOfGenTrFitness;
    
    protected float[] meanMDD;
    protected float[] sdMDD;
    
    private double[] bestTrainingSemantics;
    private double[] bestTestSemantics;
        
    protected int currentGeneration;
    // ========================= ADDED FOR GECCO PAPER =========================
//    private ArrayList<int[]> trGeTarget;
//    private ArrayList<int[]> tsGeTarget;
    // =========================================================================
    
    public Statistics(int numGenerations, ExperimentalData expData) {
        bestOfGenSize = new String[numGenerations+1];
        bestOfGenTsFitness = new String[numGenerations+1];
        bestOfGenTrFitness = new String[numGenerations+1];
        meanMDD = new float[numGenerations+1];
        sdMDD = new float[numGenerations+1];
        currentGeneration = 0;
        this.expData = expData;
        
        // ======================= ADDED FOR GECCO PAPER =======================
//        trGeTarget = new ArrayList<>();
//        tsGeTarget = new ArrayList<>();
        // =====================================================================
    }
    
    // ========================= ADDED FOR GECCO PAPER =========================
//    public void storeDristInfo(Population pop){
//        
//        if(currentGeneration > 0 && (currentGeneration-1) % 10 == 0){
//        
//            int[] tsGE = new int[expData.getDataset(Utils.DatasetType.TEST).size()];
//            int[] trGE = new int[expData.getDataset(Utils.DatasetType.TRAINING).size()];
//            for(Individual ind : pop){
//                double[] tsSem = ind.getTestSemantics();
//                double[] trSem = ind.getTrainingSemantics();
//                for(int i = 0; i < tsSem.length; i++){
//                    if(tsSem[i] >= expData.getDataset(Utils.DatasetType.TEST).getOutputs()[i])
//                        tsGE[i]++;
//                }
//                for(int i = 0; i < trSem.length; i++){
//                    if(trSem[i] >= expData.getDataset(Utils.DatasetType.TRAINING).getOutputs()[i])
//                        trGE[i]++;
//                }
//            }
//
//            tsGeTarget.add(tsGE);
//            trGeTarget.add(trGE);
//            
//        }
//    }
    // =========================================================================
    
    /**
     * Update the statistics with information obtained in the end of the generation
     * @param pop Current population
     */
    public void addGenerationStatistic(Population pop){        
        // In order to not contabilize the time elapsed by this method we subtract
        // the time elapsed
        long methodTime = System.nanoTime();
        
        Individual bestOfGen = pop.getBestIndividual();
       
        bestOfGenSize[currentGeneration] = bestOfGen.getNumNodesAsString();
        bestOfGenTrFitness[currentGeneration] = bestOfGen.getTrainingFitnessAsString();
        bestOfGenTsFitness[currentGeneration] = bestOfGen.getTestFitnessAsString();
        
        computeMDD(pop);
        
        System.out.println("Best of Gen (RMSE-TR/RMSE-TS/nodes: " + bestOfGenTrFitness[currentGeneration] + 
                           "/" + bestOfGenTsFitness[currentGeneration] + "/" + bestOfGenSize[currentGeneration]);
        currentGeneration++;
        
        // Ignore the time elapsed to store the statistics
        elapsedTime += System.nanoTime() - methodTime;
    }

    public void finishEvolution(Individual bestIndividual) {
        elapsedTime = System.nanoTime() - elapsedTime;
        // Convert nanosecs to secs
        elapsedTime /= 1000000000;
        
        bestTestSemantics = ((GSGPIndividual)bestIndividual).getTestSemantics();
        bestTrainingSemantics = bestIndividual.getTrainingSemantics();
    }
    
    public String asWritableString(StatsType type) {
        switch(type){
            case BEST_OF_GEN_SIZE:
                return concatenateArray(bestOfGenSize);
            case BEST_OF_GEN_TR_FIT:
                return concatenateArray(bestOfGenTrFitness);
            case SEMANTICS:
                return getSemanticsAsString();
            case BEST_OF_GEN_TS_FIT:
                return concatenateArray(bestOfGenTsFitness);
            case ELAPSED_TIME:
                return elapsedTime + "";
            case MDD_AVG:
                return concatenateFloatArray(meanMDD);
            case MDD_SD:
                return concatenateFloatArray(sdMDD);
            default:
                return null;
        }
    }
    
    private String concatenateArray(String[] stringArray){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < stringArray.length-1; i++){
            str.append(stringArray[i] + ",");
        }
        str.append(stringArray[stringArray.length-1]);        
        return str.toString();
    }
    
    private String concatenateFloatArray(float[] floatArray) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < floatArray.length-1; i++){
            str.append(Utils.format(floatArray[i]) + ",");
        }
        str.append(Utils.format(floatArray[floatArray.length-1]));        
        return str.toString();
    }
        
    private String getSemanticsAsString() {
        StringBuffer str = new StringBuffer();
        // ======================= ADDED FOR GECCO PAPER =======================
//        for(int[] trGE : trGeTarget){
//            for(int i = 0; i < trGE.length; i++){
//                str.append(trGE[i] + ",");
//            }
//        
//        }
        // =====================================================================
        
        for(int i = 0; i < bestTrainingSemantics.length; i++){
            str.append(bestTrainingSemantics[i] + ",");
        }
        
        String sep = "";
        // ======================= ADDED FOR GECCO PAPER =======================
//        for(int[] tsGE : tsGeTarget){
//            for(int i = 0; i < tsGE.length; i++){
//                str.append(sep + tsGE[i]);
//                sep = ",";
//            }
//        }
        // =====================================================================
            
        for(int i = 0; i < bestTestSemantics.length; i++){
            str.append(sep + bestTestSemantics[i]);
            sep = ",";
        }
        
        return str.toString();
    }
    
    public void startClock(){
        elapsedTime = System.nanoTime();
    }
    
    private void computeMDD(Population pop) {
        // Target vector
        double[] t = expData.getDataset(Utils.DatasetType.TRAINING).getOutputs();
        // Store the counting of individuals greater or equal to the target in each dimension
        float[] ge = new float[t.length];
        for(Individual ind : pop){
            for(int i = 0; i < ge.length; i++){
                if(ind.getTrainingSemantics()[i] >= t[i]){
                    ge[i]++;
                }
            }
        }
        float mean = 0;
        double sd = 0;
        for(int i = 0; i < ge.length; i++){
            ge[i] /= pop.size();
            ge[i] = (float)Math.abs(ge[i]-0.5);
            mean += ge[i];
        }
        mean /= (ge.length);
        for(int i = 0; i < ge.length; i++){
            double aux = ge[i] - mean;
            sd += aux*aux;
        }
        sd /= ge.length-1;
        sd = Math.sqrt(sd);
        meanMDD[currentGeneration] = mean;
        sdMDD[currentGeneration] = (float)sd;
    }
    
    
}
