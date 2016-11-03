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
import java.util.Collections;
import java.util.PriorityQueue;

/**
 *
 * @author casadei
 */
public class PercentileMinMaxStrategy extends MinMaxStrategy {
    private final static double DISCARTED = 0.1;

    public PercentileMinMaxStrategy() {
        super();
    }

    public PercentileMinMaxStrategy(NormalizationStrategy before) {
        super(before);
    }
    
    @Override
    public void setup(Dataset dataset, Node tree) {
        super.setup(dataset, tree);
        int capacity = (int) Math.floor(dataset.size() * DISCARTED);
        
        PriorityQueue<Double> lowest =  new PriorityQueue<>(capacity, Collections.reverseOrder());
        PriorityQueue<Double> highest=  new PriorityQueue<>(capacity, Collections.reverseOrder());
        
        fillPriorityQueues(dataset, lowest, highest, capacity);
        
        min = lowest.peek();
        max = highest.peek();
        
        lowest.clear();
        highest.clear();
    }    
    
    private void fillPriorityQueues(
            Dataset dataset,
            PriorityQueue<Double> lowest, 
            PriorityQueue<Double> highest, 
            int capacity
    ) { 
        double value;
       
        for (Instance instance : dataset) {
            value = eval(instance);
            
            if (lowest.size() < capacity)
                lowest.add(value);
            else if (lowest.peek() > value) {
                lowest.poll();
                lowest.add(value);
            }
            
            if (highest.size() < capacity)
                highest.add(value);
            else if (highest.peek() < value) {
                highest.poll();
                highest.add(value);          
            }
        }
        
    }
}
