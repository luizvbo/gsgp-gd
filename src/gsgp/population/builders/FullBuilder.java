/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package gsgp.population.builders;
import gsgp.MersenneTwister;
import gsgp.nodes.Node;
import gsgp.nodes.functions.Function;
import gsgp.nodes.terminals.Terminal;



public class FullBuilder extends IndividualBuilder {

    public FullBuilder(final int maxDepth, 
                       final int minDepth, 
                       final Function[] functions,
                       final Terminal[] terminals) {
        super(maxDepth, minDepth, functions, terminals);
    }

    @Override
    public Node newRootedTree(final int current, MersenneTwister rnd){
        return fullNode(0, maxDepth, rnd);
    }
}
