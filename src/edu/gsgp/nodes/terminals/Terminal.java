/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.nodes.terminals;

import edu.gsgp.MersenneTwister;
import edu.gsgp.nodes.Node;

/**
 *
 * @author luiz
 */
public interface Terminal extends Node{
//    public void setup(MersenneTwister rnd);
    public Terminal softClone(MersenneTwister rnd);
}
