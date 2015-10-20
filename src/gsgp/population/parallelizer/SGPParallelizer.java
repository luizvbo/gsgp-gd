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
import gsgp.population.SGPIndividual;

/**
 *
 * @author luiz
 */
public class SGPParallelizer extends Thread{
    protected ArrayList<Individual> localPop;
    protected static PropertiesManager properties;
    protected static ExperimentDataset experimentalData;
    protected static Population population;
    private final double mutationStep;
    protected int size;

    protected SGPParallelizer(int localPopSize,
                            PropertiesManager properties, 
                            ExperimentDataset dataset, 
                            Population population,
                            double ms) {
        size = localPopSize;
        this.properties = properties;
        this.experimentalData = dataset;
        this.population = population;
        mutationStep = ms;
        localPop = new ArrayList<>();
    }

    
    public static SGPParallelizer[] getParallelizers(int totalSize,
                                                    PropertiesManager properties, 
                                                    ExperimentDataset dataset, 
                                                    Population population, 
                                                    double ms){
        int numberOfThreads = Math.min(properties.getNumThreads(), totalSize);
        SGPParallelizer[] genParallel = new SGPParallelizer[numberOfThreads];
        double individualsPerThread = totalSize / (double)numberOfThreads;
        double lastThreadSize = 0;
        for(int i = 0; i <  numberOfThreads; i++){
            int size = (int)Math.round((i+1)*individualsPerThread - lastThreadSize);
            genParallel[i] = new SGPParallelizer(size, properties, dataset, population, ms);
            lastThreadSize += size;
        }
        return genParallel;
    }
    
    @Override
    public void run() {
        localPop.clear();
        if(!population.isInitialized()){
            for(int i = 0; i < size; i++){
                SGPIndividual newIndividual = new SGPIndividual(properties.getNewIndividualTree(), experimentalData);
                localPop.add(newIndividual);
                evaluateNewIndividual(newIndividual, TargetType.TRAINING);
                evaluateNewIndividual(newIndividual, TargetType.TEST);
            }
        }
        else{
            for(int i = 0; i < size; i++){
                double floatDice = properties.getMersennePRNG().nextDouble();
                SGPIndividual newIndividual;
                if(floatDice < properties.getXoverProb()){
                    newIndividual = semanticXover();
                }
                else if(floatDice < properties.getXoverProb() + properties.getMutProb()){
                    newIndividual = (SGPIndividual)properties.selectIndividual(population);
                    newIndividual = semanticMutation(newIndividual);
                }
                else{
                    newIndividual = (SGPIndividual)properties.selectIndividual(population);
                }
                localPop.add(newIndividual);
            }
        }
    }

    protected void evaluateNewIndividual(SGPIndividual individual, TargetType target) {
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

    private SGPIndividual semanticXover() {
        SGPIndividual p1 = (SGPIndividual)properties.selectIndividual(population);
        SGPIndividual p2 = (SGPIndividual)properties.selectIndividual(population);
        while(p1.equals(p2)) p2 = (SGPIndividual)properties.selectIndividual(population);
        Node rt = properties.getRandomTree();
        
//        Function newTree = new SGXover();
//        newTree.addNode(rt, 0);
//        newTree.addNode(p1.getTree(), 1);
//        newTree.addNode(p2.getTree(), 2);

        double[] rtTrSemantics = Utils.getSemantics(experimentalData.training, rt);
        double[] rtTsSemantics = Utils.getSemantics(experimentalData.test, rt);
        
        double[] newTrSemantics = new double[experimentalData.training.size()];
        double[] newTsSemantics = new double[experimentalData.test.size()];
        
        double trSS_res = 0;
        double tsSS_res = 0;
        
        for(int i = 0; i < experimentalData.training.size(); i++){
            double r = Utils.sigmoid(rtTrSemantics[i]);
            newTrSemantics[i] = r*p1.getTrSemantics()[i] + (1-r)*p2.getTrSemantics()[i];
            
            double temp = newTrSemantics[i] - experimentalData.training.get(i).output;
            trSS_res += temp * temp;
        }
        
        
        for(int i = 0; i < experimentalData.test.size(); i++){
            double r = Utils.sigmoid(rtTsSemantics[i]);
            newTsSemantics[i] = r*p1.getTsSemantics()[i] + (1-r)*p2.getTsSemantics()[i];
            
            double temp = newTsSemantics[i] - experimentalData.test.get(i).output;
            tsSS_res += temp * temp;
        }

        SGPIndividual newInd = new SGPIndividual(experimentalData, p1.getNumNodes().add(p2.getNumNodes()).add(new BigInteger(rt.getNumNodes() + "")).add(BigInteger.ONE));
        newInd.setTrSemantics(newTrSemantics);
        newInd.setTsSemantics(newTsSemantics);
        
        newInd.setTrRMSE(Math.sqrt(trSS_res/experimentalData.training.size()));
        newInd.setTsRMSE(Math.sqrt(tsSS_res/experimentalData.test.size()));

        return newInd;
    }

    private SGPIndividual semanticMutation(SGPIndividual individual) {
        Node rt1 = properties.getRandomTree();
        Node rt2 = properties.getRandomTree();

//        Function newTree = new SGMutation(ms);
//        newTree.addNode(rt1, 0);
//        newTree.addNode(rt2, 1);
//        newTree.addNode(individual.getTree(), 2);

        double[] rt1TrSemantics = Utils.getSemantics(experimentalData.training, rt1);
        double[] rt1TsSemantics = Utils.getSemantics(experimentalData.test, rt1);
        
        double[] rt2TrSemantics = Utils.getSemantics(experimentalData.training, rt2);
        double[] rt2TsSemantics = Utils.getSemantics(experimentalData.test, rt2);
       
        double[] newTrSemantics = new double[experimentalData.training.size()];
        double[] newTsSemantics = new double[experimentalData.test.size()];
        
        double trSS_res = 0;
        double tsSS_res = 0;
        
        for(int i = 0; i < experimentalData.training.size(); i++){
            double r = Utils.sigmoid(rt1TrSemantics[i]);
            r -=  Utils.sigmoid(rt2TrSemantics[i]);
            newTrSemantics[i] = individual.getTrSemantics()[i] + mutationStep*r;
            
            double temp = newTrSemantics[i] - experimentalData.training.get(i).output;
            trSS_res += temp * temp;
        }
        for(int i = 0; i < experimentalData.test.size(); i++){
            double r = Utils.sigmoid(rt1TsSemantics[i]);
            r -=  Utils.sigmoid(rt2TsSemantics[i]);
            newTsSemantics[i] = individual.getTsSemantics()[i] + mutationStep*r;
            
            double temp = newTsSemantics[i] - experimentalData.test.get(i).output;
            tsSS_res += temp * temp;
        }

        SGPIndividual newInd = new SGPIndividual(experimentalData, 
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
