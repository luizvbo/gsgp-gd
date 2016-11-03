/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsgp.normalization.strategies;

import edu.gsgp.experiment.data.Dataset;
import edu.gsgp.experiment.data.Instance;
import edu.gsgp.nodes.Node;
import edu.gsgp.normalization.NormalizationStrategy;

/**
 *
 * @author casadei
 */
public class MinMaxStrategy extends NormalizationStrategy {
    protected double min = Double.MAX_VALUE;
    protected double max = Double.MIN_VALUE;

    public MinMaxStrategy() {
        super();
    }

    public MinMaxStrategy(NormalizationStrategy before) {
        super(before);
    }
    
    @Override
    public void setup(Dataset dataset, Node tree) {
        super.setup(dataset, tree);
        
        double value;
        for (Instance instance : dataset) {
            value = tree.eval(instance.input);
            
            min = Math.min(min, value);
            max = Math.max(min, value);
        }
    }
        
    @Override
    public double normalize(Instance instance) {
        if (min == max)
            return 0.5;
        
        return Math.min(1, Math.max(0, (eval(instance) - min) / (max - min)));
    }
}
