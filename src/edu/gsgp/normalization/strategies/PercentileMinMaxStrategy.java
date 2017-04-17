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
import java.util.Comparator;
import java.util.PriorityQueue;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author casadei
 */
public class PercentileMinMaxStrategy extends MinMaxStrategy {
    private final double evaluatedPercentual;

    public PercentileMinMaxStrategy(double evaluatedPercentual) {
        super();
        
        this.evaluatedPercentual = evaluatedPercentual;
    }

    public PercentileMinMaxStrategy(double evaluatedPercentual, NormalizationStrategy before) {
        super(before);
        
        this.evaluatedPercentual = evaluatedPercentual;
    }
        
    @Override
    public void setup(Dataset dataset, Node tree) {
        super.setup(dataset, tree);
        int capacity = Math.max(1, (int) Math.floor(dataset.size() * getDiscartedPercentualPerTail()));
        
        PriorityQueue<Double> lowest  = new PriorityQueue<>(capacity, new Comparator<Double>() {
            public int compare(Double lhs, Double rhs) {
                if (lhs > rhs) return +1;
                if (lhs.equals(rhs)) return 0;
                return -1;
            }
        });
        PriorityQueue<Double> highest = new PriorityQueue<>(capacity);
        
        fillPriorityQueues(dataset, lowest, highest, capacity);
        
        min = lowest.peek();
        max = highest.peek();
        
        lowest.clear();
        highest.clear();
    }    
    
    private double getDiscartedPercentualPerTail() {
        return (1 - evaluatedPercentual / 100) / 2.0;
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
