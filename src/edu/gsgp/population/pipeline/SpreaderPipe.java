/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.pipeline;

import edu.gsgp.data.ExperimentalData;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;
import edu.gsgp.population.builder.individual.Breeder;
import edu.gsgp.population.builder.individual.GLBreeder;

/**
 *
 * @author luiz
 */
public class SpreaderPipe extends Pipeline{    
    private int currentGen;

    public SpreaderPipe() {
        currentGen = 0;
    }
    
    @Override
    public Population evolvePopulation(Population originalPop, ExperimentalData expData, int size) {
        // Update the breeder with the current population before generating a new one
        for(Breeder breeder : breederArray) ((Breeder)breeder).setup(originalPop, expData);     
        GLBreeder spreader = new GLBreeder(properties, 0.0);
        spreader.setup(originalPop, expData, currentGen++);
        
        // Generate the new population from the original one
        Population newPopulation = new Population();
        for(int i = 0; i < size; i++){
            double floatDice = rndGenerator.nextDouble();
            double probabilitySum = 0;
            Breeder selectedBreeder = breederArray[0];
            for (Breeder breeder : breederArray) {
                if (floatDice < probabilitySum + breeder.getProbability()) {
                    selectedBreeder = breeder;
                    break;
                }
                probabilitySum += breeder.getProbability();
            }
            Individual newInd = selectedBreeder.generateIndividual(rndGenerator, expData);
            floatDice = rndGenerator.nextDouble();
            if(floatDice < spreader.getEffectiveProb()){
                newInd = spreader.generateIndividual(rndGenerator, expData);
            }
            newPopulation.add(newInd);
        }        
        return newPopulation;
    }

    @Override
    public Pipeline softClone() {
        return new SpreaderPipe();
    }
}
