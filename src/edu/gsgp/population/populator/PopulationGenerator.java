/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.populator;

import edu.gsgp.MersenneTwister;
import edu.gsgp.population.Population;
import edu.gsgp.data.PropertiesManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 * 
 * Generate a population using parallelis
 */
public class PopulationGenerator extends Thread{
    protected PropertiesManager properties;
    
//    private final IndividualBuilder[] indBuilders;
    protected int popSize;
    
    
    public PopulationGenerator(PropertiesManager properties,
//                               IndividualBuilder[] indBuilders,
                               int popSize) {
        this.properties = properties;
        this.popSize = popSize;
//        this.indBuilders = indBuilders;
    }
        
//    public Population populate() throws Exception{
//        int numThreads = Math.min(properties.getNumThreads(), popSize);
//        ParallelGenerator[] parallelGenerators = new ParallelGenerator[numThreads];
//        MersenneTwister rndGenerators[] = properties.getMersennePRGNArray(numThreads);
//        double individualsPerThread = popSize / (double)numThreads;
//        double lastThreadSize = 0;
//        for(int i = 0; i <  numThreads; i++){
//            int size = (int)Math.round((i+1)*individualsPerThread - lastThreadSize);
//            parallelGenerators[i] = new ParallelGenerator(rndGenerators[i], size);
//            lastThreadSize += size;
//        }
//        
//        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
//        for (ParallelGenerator parallelizer : parallelGenerators) {
//            executor.execute(parallelizer);
//        }
//        executor.shutdown();
//        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
//        
//        return getPopulation(parallelGenerators);
//    }
//
//    private Population getPopulation(ParallelGenerator[] parallelGenerators) {
//        Population pop = new Population();
//        for(ParallelGenerator parallelGen : parallelGenerators){
//            pop.addAll(parallelGen.getLocalPopulation());
//        }
//        return pop;
//    }       
//    
//    /**
//     * Class used to generate inidividuals in parallel
//     */
//    private class ParallelGenerator extends Thread{
//        private MersenneTwister rndGenerator;
//        private Population localPop;
//        private int popSize;
//
//        public ParallelGenerator(MersenneTwister rndGenerator, int popSize) {
//            this.rndGenerator = rndGenerator;
//            this.popSize = popSize;
//            localPop = new Population();
//        }      
//
//        @Override
//        public void run() {
//            if(indBuilders[0] instanceof Populator){
//                Populator populator = (Populator)indBuilders[0];
//                localPop = populator.populate(rndGenerator, popSize);
//            }
//            else{
//                for(int i = 0; i < popSize; i++){
//                    double floatDice = rndGenerator.nextDouble();
//                    double probabilitySum = 0;
//                    for (IndividualBuilder indBuilder : indBuilders) {
//                        Breeder breeder = (Breeder)indBuilder;
//                        if (floatDice < probabilitySum + breeder.getProbability()) {
//                            localPop.add(breeder.generateIndividual(rndGenerator));
//                            break;
//                        }
//                        probabilitySum += breeder.getProbability();
//                    }
//                }
//            }
//        }
//        
//        public Population getLocalPopulation() {
//            return localPop;
//        }
//    }
}
