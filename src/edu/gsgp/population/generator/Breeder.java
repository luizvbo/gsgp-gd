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
public abstract class Breeder {
    protected double probability;
    protected PropertiesManager properties;
    protected Population population;
        
    public double getProbability() {
        return probability;
    }

    public Breeder(double probability,
                   PropertiesManager properties,
                   Population population) {
        this.probability = probability;
        this.properties = properties;
        this.population = population;
    }
    
    public abstract Individual generateIndividual(MersenneTwister rndGenerator);
}
