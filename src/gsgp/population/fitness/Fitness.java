/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gsgp.population.fitness;

import gsgp.nodes.Node;


/**
 *
 * @author luiz
 */
public interface Fitness{
//    public void calculateFitness(Node tree);
    public double getComparableValue();
}
