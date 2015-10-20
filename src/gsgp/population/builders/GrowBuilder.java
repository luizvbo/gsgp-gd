/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package gsgp.population.builders;
import gsgp.MersenneTwister;
import gsgp.nodes.Node;
import gsgp.nodes.functions.Function;



public class GrowBuilder extends IndividualBuilder {

    public GrowBuilder(final int maxDepth, 
                       final int minDepth, 
                       final Function[] functions,
                       final Node[] terminals,
                       final MersenneTwister rnd) {
        super(maxDepth, minDepth, functions, terminals, rnd);
    }
    
    @Override
    public Node newRootedTree(final int current){
        return growNode(0, maxDepth);
    }
}


