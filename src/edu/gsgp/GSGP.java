/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp;

import edu.gsgp.data.ExperimentalData;
import edu.gsgp.population.Population;
import edu.gsgp.population.Individual;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.population.populator.Populator;
import edu.gsgp.population.pipeline.Pipeline;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class GSGP {
    private final PropertiesManager properties;
    private final Statistics statistics;
    private final ExperimentalData expData;
    private final MersenneTwister rndGenerator;

    public GSGP(PropertiesManager properties, ExperimentalData expData) throws Exception{
        this.properties = properties;
        this.expData = expData;
        statistics = new Statistics(properties.getNumGenerations(), expData);
        rndGenerator = properties.getRandomGenerator();
    }
    
    public void evolve() throws Exception{
        boolean canStop = false;     
        
        Populator populator = properties.getPopulationInitializer();
        Pipeline pipe = properties.getPipeline();
        
        statistics.startClock();
        
        Population population = populator.populate(rndGenerator, expData, properties.getPopulationSize());
        pipe.setup(properties, statistics, expData, rndGenerator);
        
        statistics.addGenerationStatistic(population);
        
        for(int i = 0; i < properties.getNumGenerations() && !canStop; i++){
            System.out.println("Generation " + (i+1) + ":");
                        
            // Evolve a new Population
            Population newPopulation = pipe.evolvePopulation(population, expData, properties.getPopulationSize()-1);
            // The first position is reserved for the best of the generation (elitism)
            newPopulation.add(population.getBestIndividual());
            Individual bestIndividual = newPopulation.getBestIndividual();
            if(bestIndividual.isBestSolution(properties.getMinError())) canStop = true;
            population = newPopulation;
            
            statistics.addGenerationStatistic(population);
        }
        statistics.finishEvolution(population.getBestIndividual());
    }

    public Statistics getStatistics() {
        return statistics;
    }
}
