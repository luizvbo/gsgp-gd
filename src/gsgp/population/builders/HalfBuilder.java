/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package gsgp.population.builders;
import gsgp.MersenneTwister;
import gsgp.nodes.Node;
import gsgp.nodes.functions.Function;

/* 
 * HalfBuilder.java
 * 
 * Created: Thu Oct  7 18:03:49 1999
 * By: Sean Luke
 */

/** HalfBuilder is a GPNodeBuilder which 
    implements the RAMPED HALF-AND-HALF tree building method described in Koza I/II.  
    <p><b>Default Base</b><br>
    gp.koza.half

    * @author Sean Luke
    * @version 1.0 
    */


public class HalfBuilder extends IndividualBuilder{

    public HalfBuilder(final int maxDepth, 
                       final int minDepth, 
                       final Function[] functions,
                       final Node[] terminals,
                       final MersenneTwister rnd) {
        super(maxDepth, minDepth, functions, terminals, rnd);
    }
    
    @Override
    public Node newRootedTree(final int current){
        if (rnd.nextBoolean())
            return growNode(0, rnd.nextInt(maxDepth-minDepth+1) + minDepth);
        else
            return fullNode(0, rnd.nextInt(maxDepth-minDepth+1) + minDepth);
        }

    }


