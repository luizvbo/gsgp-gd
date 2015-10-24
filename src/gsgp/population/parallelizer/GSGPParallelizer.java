/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gsgp.population.parallelizer;

import java.math.BigInteger;
import java.util.ArrayList;
import gsgp.population.Population;
import gsgp.Utils;
import gsgp.Utils.TargetType;
import gsgp.data.Dataset;
import gsgp.data.ExperimentDataset;
import gsgp.data.Instance;
import gsgp.data.PropertiesManager;
import gsgp.nodes.Node;
import gsgp.population.Individual;
import gsgp.population.GSGPIndividual;

/**
 *
 * @author luiz
 */
public class GSGPParallelizer extends Thread{
    protected ArrayList<Individual> localPop;
    protected static PropertiesManager properties;
    protected static ExperimentDataset experimentalData;
    protected static Population population;
    protected final double mutationStep;
    protected int size;
    
    protected GSGPParallelizer(int localPopSize, double ms) {
        size = localPopSize;
        mutationStep = ms;
        localPop = new ArrayList<>();
    }
    
    /**
     * Initalize the static parameters of the class
     * @param properties Properties manager
     * @param dataset Training/test data
     * @param population Initial population
     */
    public static void initializerParallelizer(PropertiesManager properties, 
                                            ExperimentDataset dataset, 
                                            Population population){
        GSGPParallelizer.properties = properties;
        GSGPParallelizer.experimentalData = dataset;
        GSGPParallelizer.population = population;
    }
    
    public static GSGPParallelizer[] getParallelizers(int totalSize, double ms){
        int numberOfThreads = Math.min(properties.getNumThreads(), totalSize);
        GSGPParallelizer[] genParallel = new GSGPParallelizer[numberOfThreads];
        double individualsPerThread = totalSize / (double)numberOfThreads;
        double lastThreadSize = 0;
        for(int i = 0; i <  numberOfThreads; i++){
            int size = (int)Math.round((i+1)*individualsPerThread - lastThreadSize);
            genParallel[i] = new GSGPParallelizer(size, ms);
            lastThreadSize += size;
        }
        return genParallel;
    }
    
    @Override
    public void run() {
        localPop.clear();
        if(!population.isInitialized()){
            for(int i = 0; i < size; i++){
                GSGPIndividual newIndividual = new GSGPIndividual(properties.getNewIndividualTree(), experimentalData);
                localPop.add(newIndividual);
                evaluateNewIndividual(newIndividual, TargetType.TRAINING);
                evaluateNewIndividual(newIndividual, TargetType.TEST);
            }
        }
        else{
            for(int i = 0; i < size; i++){
                double floatDice = properties.getMersennePRNG().nextDouble();
                GSGPIndividual newIndividual;
                if(floatDice < properties.getXoverProb()){
                    newIndividual = semanticXover();
                }
                else if(floatDice < properties.getXoverProb() + properties.getMutProb()){
                    newIndividual = (GSGPIndividual)properties.selectIndividual(population);
                    newIndividual = semanticMutation(newIndividual);
                }
                else{
                    newIndividual = (GSGPIndividual)properties.selectIndividual(population);
                }
                localPop.add(newIndividual);
            }
        }
    }

    protected void evaluateNewIndividual(GSGPIndividual individual, TargetType target) {
        Dataset data;
        if(target == TargetType.TRAINING) data = experimentalData.training;
        else data = experimentalData.test;
        double ss_res = 0;
        double[] newSemantics = new double[data.size()];
        for(int i = 0; i < data.size(); i++){
            Instance inst = data.get(i);
            newSemantics[i] = individual.eval(inst.input);
            double temp = newSemantics[i] - inst.output;
            ss_res += temp * temp;
        }
        ss_res = Math.sqrt(ss_res/data.size());
        if(target == TargetType.TRAINING){
            individual.setTrRMSE(ss_res);
            individual.setTrSemantics(newSemantics);
            individual.startNumNodes();
        }
        else{
            individual.setTsRMSE(ss_res);
            individual.setTsSemantics(newSemantics);
        }
    }

    protected GSGPIndividual semanticXover() {
        GSGPIndividual p1 = (GSGPIndividual)properties.selectIndividual(population);
        GSGPIndividual p2 = (GSGPIndividual)properties.selectIndividual(population);
        while(p1.equals(p2)) p2 = (GSGPIndividual)properties.selectIndividual(population);
        Node rt = properties.getRandomTree();
        
//        Function newTree = new SGXover();
//        newTree.addNode(rt, 0);
//        newTree.addNode(p1.getTree(), 1);
//        newTree.addNode(p2.getTree(), 2);

        // Compute the (training/test) semantics of generated random tree
        double[] rtTrSemantics = Utils.getSemantics(experimentalData.training, rt);
        double[] rtTsSemantics = Utils.getSemantics(experimentalData.test, rt);
        
        // These two arrays are used to store the new semantics (training/test) of
        // the resulting individual
        double[] newTrSemantics = new double[experimentalData.training.size()];
        double[] newTsSemantics = new double[experimentalData.test.size()];
        
        double trSS_res = 0;
        double tsSS_res = 0;
        
        // Compute the (training) semantics of the new individual
        for(int i = 0; i < experimentalData.training.size(); i++){
            double r = Utils.sigmoid(rtTrSemantics[i]);
            newTrSemantics[i] = r*p1.getTrainingSemantics()[i] + (1-r)*p2.getTrainingSemantics()[i];
            
            double temp = newTrSemantics[i] - experimentalData.training.get(i).output;
            trSS_res += temp * temp;
        }
        // Compute the (test) semantics of the new individual
        for(int i = 0; i < experimentalData.test.size(); i++){
            double r = Utils.sigmoid(rtTsSemantics[i]);
            newTsSemantics[i] = r*p1.getTestSemantics()[i] + (1-r)*p2.getTestSemantics()[i];
            
            double temp = newTsSemantics[i] - experimentalData.test.get(i).output;
            tsSS_res += temp * temp;
        }

        GSGPIndividual newInd = new GSGPIndividual(experimentalData, p1.getNumNodes().add(p2.getNumNodes()).add(new BigInteger(rt.getNumNodes() + "")).add(BigInteger.ONE));
        newInd.setTrSemantics(newTrSemantics);
        newInd.setTsSemantics(newTsSemantics);
        
        newInd.setTrRMSE(Math.sqrt(trSS_res/experimentalData.training.size()));
        newInd.setTsRMSE(Math.sqrt(tsSS_res/experimentalData.test.size()));

        return newInd;
    }

    protected GSGPIndividual semanticMutation(GSGPIndividual individual) {
        Node rt1 = properties.getRandomTree();
        Node rt2 = properties.getRandomTree();

//        Function newTree = new SGMutation(ms);
//        newTree.addNode(rt1, 0);
//        newTree.addNode(rt2, 1);
//        newTree.addNode(individual.getTree(), 2);
        
        // Compute the (training/test) semantics of generated random trees
        double[] rt1TrSemantics = Utils.getSemantics(experimentalData.training, rt1);
        double[] rt1TsSemantics = Utils.getSemantics(experimentalData.test, rt1);
        
        double[] rt2TrSemantics = Utils.getSemantics(experimentalData.training, rt2);
        double[] rt2TsSemantics = Utils.getSemantics(experimentalData.test, rt2);
       
        // These two arrays are used to store the new semantics (training/test) of
        // the resulting individual
        double[] newTrSemantics = new double[experimentalData.training.size()];
        double[] newTsSemantics = new double[experimentalData.test.size()];
        
        double trSS_res = 0;
        double tsSS_res = 0;
        
        // Compute the (training) semantics of the new individual
        for(int i = 0; i < experimentalData.training.size(); i++){
            double r = Utils.sigmoid(rt1TrSemantics[i]);
            r -=  Utils.sigmoid(rt2TrSemantics[i]);
            newTrSemantics[i] = individual.getTrainingSemantics()[i] + mutationStep*r;
            
            double temp = newTrSemantics[i] - experimentalData.training.get(i).output;
            trSS_res += temp * temp;
        }
        // Compute the (test) semantics of the new individual
        for(int i = 0; i < experimentalData.test.size(); i++){
            double r = Utils.sigmoid(rt1TsSemantics[i]);
            r -=  Utils.sigmoid(rt2TsSemantics[i]);
            newTsSemantics[i] = individual.getTestSemantics()[i] + mutationStep*r;
            
            double temp = newTsSemantics[i] - experimentalData.test.get(i).output;
            tsSS_res += temp * temp;
        }

        GSGPIndividual newInd = new GSGPIndividual(experimentalData, 
                                                 individual.getNumNodes().add(new BigInteger(rt1.getNumNodes()+"")).
                                                            add(new BigInteger(rt2.getNumNodes()+"")).add(BigInteger.ONE));
        newInd.setTrSemantics(newTrSemantics);
        newInd.setTsSemantics(newTsSemantics);
        
        newInd.setTrRMSE(Math.sqrt(trSS_res/experimentalData.training.size()));
        newInd.setTsRMSE(Math.sqrt(tsSS_res/experimentalData.test.size()));

        return newInd;
    }

    public ArrayList<Individual> getLocalPopulation() {
        return localPop;
    }
}
