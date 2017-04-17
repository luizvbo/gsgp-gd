/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsgp.normalization;

import edu.gsgp.experiment.data.Dataset;
import edu.gsgp.experiment.data.Instance;
import edu.gsgp.nodes.Node;

/**
 *
 * @author casadei
 */
public abstract class NormalizationStrategy {
    protected Node tree;
    protected NormalizationStrategy before;
    
    public NormalizationStrategy() {
        this(null);
    }
    
    public NormalizationStrategy(NormalizationStrategy before) {
        this.before = before;
    }
        
    public void setup(Dataset dataset, Node tree) {
        this.tree = tree;
        
        if (before != null)
            before.setup(dataset, tree);
    }
    public abstract double normalize(Instance instance);
    
    protected double eval(Instance instance) {
        if (before == null)
            return this.tree.eval(instance.input);
        
        return before.normalize(instance);
    }
}
