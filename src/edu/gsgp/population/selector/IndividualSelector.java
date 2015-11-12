/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.selector;

import edu.gsgp.MersenneTwister;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;

/**
 *
 * @author luiz
 */
public interface IndividualSelector {
    public Individual selectIndividual(Population population, MersenneTwister rnd);
}
