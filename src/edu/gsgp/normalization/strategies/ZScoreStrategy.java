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
public class ZScoreStrategy extends NormalizationStrategy {
    private double mean;
    private double std;

    @Override
    public void setup(Dataset dataset, Node tree) {
        super.setup(dataset, tree);
        
        double sum = 0, sumSquares = 0, value;
        for (Instance instance : dataset) {
            value = eval(instance);
            sum += value;
            sumSquares += Math.pow(value, 2);
        }
        
        mean = sum / dataset.size();
        std = Math.sqrt(sumSquares / dataset.size() - Math.pow(mean, 2));
        
        if (Double.isNaN(std))
            std = 0;        
    }
    
    @Override
    public double normalize(Instance instance) {
        if (std == 0) 
            return 0;
        
        return (eval(instance) - mean) / std;
    }
}
