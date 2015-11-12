/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package edu.gsgp.population.builders;
import edu.gsgp.MersenneTwister;
import edu.gsgp.nodes.Node;
import edu.gsgp.nodes.functions.Function;
import edu.gsgp.nodes.terminals.Terminal;



public class GrowBuilder extends IndividualBuilder {

    public GrowBuilder(final int maxDepth, 
                       final int minDepth, 
                       final Function[] functions,
                       final Terminal[] terminals) {
        super(maxDepth, minDepth, functions, terminals);
    }
    
    @Override
    public Node newRootedTree(final int current, MersenneTwister rnd){
        return growNode(0, maxDepth, rnd);
    }
}


