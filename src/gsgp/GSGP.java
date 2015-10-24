/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gsgp;

import gsgp.population.Population;
import java.util.ArrayList;
import gsgp.population.Individual;
import gsgp.data.ExperimentDataset;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import gsgp.data.PropertiesManager;
import gsgp.population.parallelizer.GSGPParallelizer;

/**
 *
 * @author luiz
 */
public class GSGP {
    private final PropertiesManager properties;
    private final ExperimentDataset dataset;
    private final Statistics statistics;
    private final Population population;

    public GSGP(ExperimentDataset dataset, PropertiesManager properties) throws Exception{
        this.dataset = dataset;
        this.properties = properties;
        population = new Population(properties);
        statistics = new Statistics(properties.getNumGenerations());
    }
    
    public void evolve() throws Exception{
        boolean canStop = false;
        
        double mutationStep = properties.getMutationStep();
        if(mutationStep == -1) mutationStep = 0.1*dataset.training.getOutputSD();
        
        // Initialize the static attributes from the parallelizer
        GSGPParallelizer.initializerParallelizer(properties, dataset, population);
        
        initializePopulation();
        
        // One member of the population is took from the previous one (elitism). That is why we subtract one
        GSGPParallelizer[] parallelizers = GSGPParallelizer.getParallelizers(properties.getPopulationSize()-1, mutationStep);
        
        ExecutorService executor;
        
        statistics.setInitialSemantics(population);
        statistics.addGenerationStatistic(population);
        
        for(int i = 0; i < properties.getNumGenerations() && !canStop; i++){
            System.out.println("Generation " + (i+1) + ":");
            executor = Executors.newFixedThreadPool(properties.getNumThreads());
            
            for (GSGPParallelizer parallelizer : parallelizers) {
                executor.execute(parallelizer);
            }
            executor.shutdown();
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            
            // Merge to compose the new Population
            ArrayList<Individual> newPopulation = new ArrayList<>();
            // The first position is reserved for the best of the generation (elitism)
            newPopulation.add(population.getBestIndividual());
            
            for(int j = 0; j < parallelizers.length; j++){
                newPopulation.addAll(parallelizers[j].getLocalPopulation());
            }            
            Collections.sort(newPopulation);
            if(newPopulation.get(0).isBestSolution(properties.getMinError())) canStop = true;
            population.setCurrentPopulation(newPopulation);
            
            statistics.addGenerationStatistic(population);
        }
    }

    public Statistics getStatistics() {
        return statistics;
    }
    
    private void initializePopulation() throws Exception{
        ExecutorService executor = Executors.newFixedThreadPool(properties.getNumThreads());
        
        GSGPParallelizer[] genParallel = GSGPParallelizer.getParallelizers(properties.getPopulationSize(), 0);
        for (GSGPParallelizer genParallel1 : genParallel) {
            executor.execute(genParallel1);
        }
        executor.shutdown();
        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        
        for(int i = 0; i < genParallel.length; i++){
            population.addAll(genParallel[i].getLocalPopulation());
        }            
        population.setInitialized(true);
    }
}
