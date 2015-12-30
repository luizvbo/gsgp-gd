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
        BEST_OF_GEN_TS_FIT("tsFitness.csv"), 
        BEST_OF_GEN_TR_FIT("trFitness.csv"), 
        SEMANTICS("outputs.csv"),
        INITIAL_SEMANTICS("initialSemantics.csv");
        
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
    
    protected int currentGeneration;
    
    public Statistics(int numGenerations, ExperimentalData expData) {
        bestOfGenSize = new String[numGenerations+1];
        bestOfGenTsFitness = new String[numGenerations+1];
        bestOfGenTrFitness = new String[numGenerations+1];
        currentGeneration = 0;
        this.expData = expData;
    }
    
    public void addGenerationStatistic(Population pop){        
        Individual bestOfGen = pop.getBestIndividual();
        
//        long[] sizes = new long[pop.size()];
//        int count = 0;
//        for(Individual ind : pop.getIndividuals()){
//            sizes[count++] = Long.parseLong(ind.getNumNodesAsString());
//        }        
//        bestOfGenSize[currentGeneration] = Utils.getMedian(sizes)+"";
        
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
        for(int i = 0; i < bestTrainingSemantics.length; i++){
            str.append(bestTrainingSemantics[i] + ",");
        }
        String sep = "";
        for(int i = 0; i < bestTestSemantics.length; i++){
            str.append(sep + bestTestSemantics[i]);
            sep = ",";
        }
        return str.toString();
    }
}
