/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.builder.individual;

import edu.gsgp.MersenneTwister;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 * 
 * Breeder methods are reponsible for the genetic operators throughout the evolution.
 */
public abstract class Breeder extends IndividualBuilder{
    protected double probability;
    protected Population originalPopulation;
    
    protected Breeder(PropertiesManager properties, double probability) {
        super(properties);
        this.probability = probability;
    }

    public double getProbability() {
        return probability;
    }
    
    public abstract Breeder softClone(PropertiesManager properties);
    
    public abstract Individual generateIndividual(MersenneTwister rndGenerator);
    
    public void setup(Population originalPopulation){
        this.originalPopulation = originalPopulation;
    }
}
