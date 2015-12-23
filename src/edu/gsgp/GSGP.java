/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp;

import edu.gsgp.population.Population;
import edu.gsgp.population.Individual;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.population.builder.individual.Breeder;
import edu.gsgp.population.builder.individual.PopulationGenerator;
import edu.gsgp.population.builder.individual.IndividualBuilder;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class GSGP {
    private final PropertiesManager properties;
    private final Statistics statistics;
    private Population population;

    public GSGP(PropertiesManager properties) throws Exception{
        this.properties = properties;
        population = new Population();
        statistics = new Statistics(properties.getNumGenerations());
    }
    
    public void evolve() throws Exception{
        boolean canStop = false;
        
        IndividualBuilder indBuilderArray[] = new IndividualBuilder[1];
        indBuilderArray[0] =  properties.getPopulationInitializer();
        
        PopulationGenerator popGenerator = new PopulationGenerator(properties, indBuilderArray, properties.getPopulationSize());
        population = popGenerator.populate();
        
//        statistics.setInitialSemantics(population);
        statistics.addGenerationStatistic(population);
        
        indBuilderArray = properties.getBreederList();
        
        for(int i = 0; i < properties.getNumGenerations() && !canStop; i++){
            System.out.println("Generation " + (i+1) + ":");
            
            // Update the breeder with the current population before generating a new one
            for(IndividualBuilder breeder : indBuilderArray) ((Breeder)breeder).setup(population);
            
            popGenerator = new PopulationGenerator(properties, indBuilderArray, properties.getPopulationSize()-1);
            
            // Merge to compose the new Population
            Population newPopulation = popGenerator.populate();
            // The first position is reserved for the best of the generation (elitism)
            newPopulation.add(population.getBestIndividual());
            Individual bestIndividual = newPopulation.getBestIndividual();
            if(bestIndividual.isBestSolution(properties.getMinError())) canStop = true;
            population = newPopulation;
            
            statistics.addGenerationStatistic(population);
        }
        statistics.storeBestSemantics(population.getBestIndividual());
    }

    public Statistics getStatistics() {
        return statistics;
    }
}
