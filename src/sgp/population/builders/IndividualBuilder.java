/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package sgp.population.builders;

import sgp.MersenneTwister;
import sgp.nodes.Node;
import sgp.nodes.functions.Function;

public abstract class IndividualBuilder {
    
    /** The largest maximum tree depth RAMPED HALF-AND-HALF can specify. */
    protected int maxDepth;

    /** The smallest maximum tree depth RAMPED HALF-AND-HALF can specify. */
    protected int minDepth;
    
    protected Function[] functions;
            
    protected Node[] terminals;
    
    protected MersenneTwister rnd;

    public IndividualBuilder(final int maxDepth, 
                             final int minDepth, 
                             final Function[] functions,
                             final Node[] terminals,
                             final MersenneTwister rnd) {
        this.maxDepth = maxDepth;
        this.minDepth = minDepth;
        this.functions = functions;
        this.terminals = terminals;
        this.rnd = rnd;
    }    
    
    public abstract Node newRootedTree(final int current);
    
    /**
     * A private recursive method which builds a FULL-style tree for newRootedTree
     * @param current Current depth
     * @param max Maximum depth
     * @return The new tree root node
     */
    protected Node fullNode(final int current, final int max){
        // pick a terminal when we're at max depth or if there are NO nonterminals
        if (current+1 >= max){                            
            return terminals[rnd.nextInt(terminals.length)].softClone();
        }
                        
        // else force a nonterminal unless we have no choice
        else{
            Function n = (Function)functions[rnd.nextInt(functions.length)].softClone();
            for(int i = 0; i < n.getArity(); i++){
                n.addNode(fullNode(current+1, max), i);
            }
            return n;
        }
    }

    /**
     * A private function which recursively returns a GROW tree to newRootedTree
     * @param current Current depth
     * @param max Maximum depth
     * @return The new tree root node
     */
    protected Node growNode(final int current, final int max) {
        // growNode can mess up if there are no available terminals for a given type.  If this occurs,
        // and we find ourselves unable to pick a terminal when we want to do so, we will issue a warning,
        // and pick a nonterminal, violating the maximum-depth contract.  This can lead to pathological situations
        // where the system will continue to go on and on unable to stop because it can't pick a terminal,
        // resulting in running out of memory or some such.  But there are cases where we'd want to let
        // this work itself out.
        
        // pick a terminal when we're at max depth or if there are NO nonterminals
        if (current+1 >= max){                                                   // AND if there are available terminals
            return terminals[rnd.nextInt(terminals.length)].softClone();
        }
                        
        // else pick a random node
        else{
            // Pick a terminal
            if(rnd.nextBoolean()){
                return terminals[rnd.nextInt(terminals.length)].softClone();
            }
            // Pick a function
            else{
                Function n = (Function)functions[rnd.nextInt(functions.length)].softClone();
                for(int i = 0; i < n.getArity(); i++){
                    n.addNode(growNode(current+1, max), i);
                }
                return n;
            }
        }
    }
}
