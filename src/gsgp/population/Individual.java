/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gsgp.population;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import gsgp.data.ExperimentDataset;
import gsgp.nodes.Node;

/**
 *
 * @author luiz
 */
public abstract class Individual implements Comparable<Individual>{
    protected Node tree;
    
    protected DecimalFormat df = new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.ENGLISH));

    public Individual(Node tree) {
        this.tree = tree;
    }   
    
    public Individual(Node tree, ExperimentDataset data) {
        this(tree);
    }
    
    public double eval(double[] input){
        return tree.eval(input);
    }

    public Node getTree() {
        return tree;
    }
    
    @Override
    public int compareTo(Individual o) {
        if (getFitness() < o.getFitness()){
            return -1;
        }
        if (getFitness() > o.getFitness()) {
            return 1;
        }
        return 0;
    }

    public boolean isBestSolution(double minError) {
        return getFitness() <= minError;
    }

    @Override
    public String toString() {
        return tree.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    public void setTree(Node randomSubtree) {
        this.tree = randomSubtree;
    }
        
    @Override
    public abstract Individual clone();
    
    public abstract double getFitness();
    
    public abstract String getNumNodesAsString();

    public abstract String getTrFitnessAsString();

    public abstract String getTsFitnessAsString();
    
    public abstract double[] getTrSemantics();
}
