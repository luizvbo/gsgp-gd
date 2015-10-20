/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gsgp.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import gsgp.data.ExperimentDataset;
import gsgp.data.PropertiesManager;
import gsgp.population.parallelizer.SGPParallelizer;


/**
 *
 * @author luiz
 */
public class Population {
    protected ArrayList<Individual> individuals;
    protected PropertiesManager properties;
    protected boolean initialized;
    
    public Population(PropertiesManager properties) {
        this.properties = properties;
        individuals = new ArrayList<>();
        initialized = false;
    }
    
    public void initialize(ExperimentDataset data) throws Exception{
        ExecutorService executor = Executors.newFixedThreadPool(properties.getNumThreads());
        SGPParallelizer[] genParallel = SGPParallelizer.getParallelizers(properties.getPopulationSize(), properties, data, this, 0);
        for (SGPParallelizer genParallel1 : genParallel) {
            executor.execute(genParallel1);
        }
        executor.shutdown();
        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        
        for(int i = 0; i < genParallel.length; i++){
            individuals.addAll(genParallel[i].getLocalPopulation());
        }            
        initialized = true;
    }
    
    public Individual getIndividual(int index){
        return individuals.get(index);
    }

    public ArrayList<Individual> getIndividuals() {
        return individuals;
    }

    public PropertiesManager getProperties() {
        return properties;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
    
    public void setCurrentPopulation(ArrayList<Individual> currentPopulation) {
        this.individuals = currentPopulation;
    }
    
    public int size(){
        return individuals.size();
    }
    
    public Individual getBestIndividual(){
        Collections.sort(individuals);
        return individuals.get(0);
    }

    public boolean isInitialized() {
        return initialized;
    }
}
