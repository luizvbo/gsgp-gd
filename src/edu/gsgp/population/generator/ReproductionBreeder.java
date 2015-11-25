/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.generator;

import edu.gsgp.MersenneTwister;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;

/**
 *
 * @author luiz
 */
public class ReproductionBreeder extends Breeder {

    public ReproductionBreeder(double probability, 
                               PropertiesManager properties, 
                               Population population) {
        super(probability, properties, population);
    }

    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator) {
        return properties.selectIndividual(population, rndGenerator).clone();
    }
    
}
