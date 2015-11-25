/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp;

import edu.gsgp.population.Population;
import edu.gsgp.population.Individual;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.population.generator.PopulationGenerator;
import edu.gsgp.Utils.DataType;
import edu.gsgp.population.generator.Breeder;
import edu.gsgp.population.generator.GSMBreeder;
import edu.gsgp.population.generator.GSXBreeder;
import edu.gsgp.population.generator.ReproductionBreeder;
import edu.gsgp.population.generator.VoidBreeder;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class GSGP {
    private final PropertiesManager properties;
    private final ExperimentalData dataset;
    private final Statistics statistics;
    private Population population;

    public GSGP(ExperimentalData dataset, PropertiesManager properties) throws Exception{
        this.dataset = dataset;
        this.properties = properties;
        population = new Population();
        statistics = new Statistics(properties.getNumGenerations());
    }
    
    public void evolve() throws Exception{
        boolean canStop = false;
        
        double mutationStep = properties.getMutationStep();
        if(mutationStep == -1) mutationStep = 0.1*dataset.getDataset(DataType.TRAINING).getOutputSD();
        
        Breeder breederArray[] = new Breeder[1];
        breederArray[0] =  new VoidBreeder(dataset, properties);
        
        PopulationGenerator popGenerator = new PopulationGenerator(properties, dataset, properties.getPopulationSize(), breederArray);
        population = popGenerator.populate();
        
//        statistics.setInitialSemantics(population);
        statistics.addGenerationStatistic(population);
        
        for(int i = 0; i < properties.getNumGenerations() && !canStop; i++){
            System.out.println("Generation " + (i+1) + ":");
            breederArray = new Breeder[3];
            breederArray[0] = new GSXBreeder(dataset, properties.getXoverProb(), properties, population);
            breederArray[1] = new GSMBreeder(dataset, mutationStep, properties.getMutProb(), properties, population);
            breederArray[2] = new ReproductionBreeder(1-(properties.getXoverProb()+properties.getMutProb()), properties, population);
            
            popGenerator = new PopulationGenerator(properties, dataset, properties.getPopulationSize()-1, breederArray);
            
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
