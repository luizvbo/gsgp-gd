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
import edu.gsgp.utils.Utils;

/**
 *
 * @author casadei
 */
public class SigmoidStrategy extends NormalizationStrategy {

    public SigmoidStrategy() {
        super();
    }    
    
    public SigmoidStrategy(NormalizationStrategy before) {
        super(before);
    }

    @Override 
    public void setup(Dataset dataset, Node tree) {
        super.setup(dataset, tree);
    }
    
    @Override
    public double normalize(Instance instance) {        
        return Utils.sigmoid(eval(instance));
    }
   
}
