/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.generator;

import edu.gsgp.MersenneTwister;
import edu.gsgp.population.Population;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.population.Individual;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class PopulationGenerator extends Thread{
    protected static PropertiesManager properties;
    protected static ExperimentalData experimentalData;
    protected static Population population;
    
    private Breeder[] breederArray;
    protected int popSize;
    protected MersenneTwister rndGenerator;
    protected int size;
    
    
    public PopulationGenerator(PropertiesManager properties,
                               ExperimentalData dataset, 
                               int popSize,
                               Breeder[] breederArray) {
        PopulationGenerator.properties = properties;
        PopulationGenerator.experimentalData = dataset;
        this.popSize = popSize;
        this.breederArray = breederArray;
    }
        
    public Population populate() throws Exception{
        int numThreads = Math.min(properties.getNumThreads(), popSize);
        ParallelGenerator[] parallelGenerators = new ParallelGenerator[numThreads];
        MersenneTwister rndGenerators[] = properties.getMersennePRGNArray(numThreads);
        double individualsPerThread = popSize / (double)numThreads;
        double lastThreadSize = 0;
        for(int i = 0; i <  numThreads; i++){
            int size = (int)Math.round((i+1)*individualsPerThread - lastThreadSize);
            parallelGenerators[i] = new ParallelGenerator(breederArray, rndGenerators[i], size);
            lastThreadSize += size;
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        for (ParallelGenerator parallelizer : parallelGenerators) {
            executor.execute(parallelizer);
        }
        executor.shutdown();
        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        
        return getPopulation(parallelGenerators);
    }

    private Population getPopulation(ParallelGenerator[] parallelGenerators) {
        Population pop = new Population();
        for(ParallelGenerator parGenerator : parallelGenerators){
            pop.addAll(parGenerator.getLocalPopulation());
        }
        return pop;
    }       
    
    private class ParallelGenerator extends Thread{
        private Breeder[] breederArray;
        private MersenneTwister rndGenerator;
        private Individual[] localPop;

        public ParallelGenerator(Breeder[] breeders, MersenneTwister rndGenerator, int popSize) {
            this.breederArray = breeders;
            this.rndGenerator = rndGenerator;
            localPop = new Individual[popSize];
        }      

        @Override
        public void run() {
            for(int i = 0; i < localPop.length; i++){
                double floatDice = rndGenerator.nextDouble();
                double probabilitySum = 0;
                for (Breeder breeder : breederArray) {
                    if (floatDice < probabilitySum + breeder.getProbability()) {
                        localPop[i] = breeder.generateIndividual(rndGenerator);
                        break;
                    }
                    probabilitySum += breeder.getProbability();
                }
            }
        }
        
        public Individual[] getLocalPopulation() {
            return localPop;
        }
    }
}
