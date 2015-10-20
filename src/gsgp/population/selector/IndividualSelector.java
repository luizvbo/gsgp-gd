/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gsgp.population.selector;

import gsgp.MersenneTwister;
import gsgp.population.Individual;
import gsgp.population.Population;

/**
 *
 * @author luiz
 */
public interface IndividualSelector {
    public Individual selectIndividual(Population population, MersenneTwister rnd);
}
