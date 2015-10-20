/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sgp.population.selector;

import sgp.MersenneTwister;
import sgp.population.Individual;
import sgp.population.Population;

/**
 *
 * @author luiz
 */
public interface IndividualSelector {
    public Individual selectIndividual(Population population, MersenneTwister rnd);
}
