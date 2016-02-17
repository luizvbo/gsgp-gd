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
        INITIAL_SEMANTICS("initialSemantics.csv"),
        BEST_OF_GEN_SIZE("individualSize.csv"), 
        SEMANTICS("outputs.csv"),
        BEST_OF_GEN_TS_FIT("tsFitness.csv"), 
        BEST_OF_GEN_TR_FIT("trFitness.csv");
        
        private final String filePath;

        private StatsType(String filePath) {
            this.filePath = filePath;
        }
        
        public String getPath(){
            return filePath;
        }
    }
    
    protected ExperimentalData expData;
    
    protected String[] bestOfGenSize;
    protected String[] bestOfGenTsFitness;
    protected String[] bestOfGenTrFitness;
    
    private double[] bestTrainingSemantics;
    private double[] bestTestSemantics;
    
    private double[][] initialSemantics;
    
    // ========================= ADDED FOR GECCO PAPER =========================
//    private ArrayList<int[]> trGeTarget;
//    private ArrayList<int[]> tsGeTarget;
    // =========================================================================
    
    
    protected int currentGeneration;
    
    public Statistics(int numGenerations, ExperimentalData expData) {
        bestOfGenSize = new String[numGenerations+1];
        bestOfGenTsFitness = new String[numGenerations+1];
        bestOfGenTrFitness = new String[numGenerations+1];
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
    
    public void addGenerationStatistic(Population pop){        
        Individual bestOfGen = pop.getBestIndividual();
       
        bestOfGenSize[currentGeneration] = bestOfGen.getNumNodesAsString();
        bestOfGenTrFitness[currentGeneration] = bestOfGen.getTrainingFitnessAsString();
        bestOfGenTsFitness[currentGeneration] = bestOfGen.getTestFitnessAsString();
        
        System.out.println("Best of Gen (RMSE-TR/RMSE-TS/nodes: " + bestOfGenTrFitness[currentGeneration] + 
                           "/" + bestOfGenTsFitness[currentGeneration] + "/" + bestOfGenSize[currentGeneration]);
                
        currentGeneration++;
    }

    private void setInitialSemantics(Population population) {
        initialSemantics = new double[population.size()][];
        int i = 0;
        for(Individual ind : population){
            initialSemantics[i++] = ind.getTrainingSemantics();
        }
    }
    
    public void storeBestSemantics(Individual bestIndividual) {
        bestTestSemantics = ((GSGPIndividual)bestIndividual).getTestSemantics();
        bestTrainingSemantics = bestIndividual.getTrainingSemantics();
    }
    
    private void resetInitialSemantics() {
        initialSemantics = null;
    }
    
    public String asWritableString(StatsType type) {
        switch(type){
            case BEST_OF_GEN_SIZE:
                return concatenateArray(bestOfGenSize);
            case BEST_OF_GEN_TR_FIT:
                return concatenateArray(bestOfGenTrFitness);
            case INITIAL_SEMANTICS:
                return concatenateMatrix(initialSemantics);
            case SEMANTICS:
                return getSemanticsAsString();
            default:
                return concatenateArray(bestOfGenTsFitness);
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
    
    private String concatenateMatrix(double[][] doubleMatrix){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < doubleMatrix.length; i++){
            for(int j = 0; j < doubleMatrix[0].length-1; j++){
                str.append(doubleMatrix[i][j] + ",");
            }
            str.append(doubleMatrix[i][doubleMatrix[0].length-1] + "\n");   
        }
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
}
