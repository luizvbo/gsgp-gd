/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gsgp;

import gsgp.population.Population;
import gsgp.population.Individual;

/**
 *
 * @author luiz
 */
public class Statistics {
    public enum StatsType{
        BEST_OF_GEN_SIZE, 
        BEST_OF_GEN_TS_FIT, 
        BEST_OF_GEN_TR_FIT, 
        SOLUTION, 
        INITIAL_SEMANTICS;
    }
    
    protected String[] bestOfGenSize;
    protected String[] bestOfGenTsFitness;
    protected String[] bestOfGenTrFitness;
    
    private double[][] initialSemantics;
    
    protected int currentGeneration;
    
    public Statistics(int numGenerations) {
        bestOfGenSize = new String[numGenerations+1];
        bestOfGenTsFitness = new String[numGenerations+1];
        bestOfGenTrFitness = new String[numGenerations+1];
        currentGeneration = 0;
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

    public void setInitialSemantics(Population population) {
        initialSemantics = new double[population.size()][];
        int i = 0;
        for(Individual ind : population.getIndividuals()){
            initialSemantics[i++] = ind.getTrainingSemantics();
        }
    }
    
    public void resetInitialSemantics() {
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
}
