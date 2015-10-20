/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sgp.nodes.terminals;

import sgp.MersenneTwister;
import sgp.nodes.Node;

/**
 *
 * @author luiz
 */
public interface Terminal extends Node{
    public void setup(MersenneTwister rnd);
}
