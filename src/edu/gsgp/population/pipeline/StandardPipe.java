/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.pipeline;

import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.population.Population;
import edu.gsgp.population.operator.Breeder;

/**
 *
 * @author luiz
 */
public class StandardPipe extends Pipeline{    
    @Override
    public Population evolvePopulation(Population originalPop, ExperimentalData expData, int size) {
        // Update the breeder with the current population before generating a new one
        for(Breeder breeder : breederArray) ((Breeder)breeder).setup(originalPop, expData);     
        
        // ======================= ADDED FOR GECCO PAPER =======================
//        stats.storeDristInfo(originalPop);
        // =====================================================================
        
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
            newPopulation.add(selectedBreeder.generateIndividual(rndGenerator, expData));
        }        
        return newPopulation;
    }

    @Override
    public Pipeline softClone() {
        return new StandardPipe();
    }
}
