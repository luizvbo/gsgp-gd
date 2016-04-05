/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.nodes.Node;
import edu.gsgp.population.fitness.Fitness;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public abstract class Individual implements Comparable<Individual>{
    protected Node tree;    
    
    protected Fitness fitnessFunction;

    public Individual(Node tree, Fitness fitnessFunction) {
        this.tree = tree;
        this.fitnessFunction = fitnessFunction;
    }   
    
    public Individual(Node tree, Fitness fitnessFunction, ExperimentalData data) {
        this(tree, fitnessFunction);
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
        
    public Fitness getFitnessFunction(){
        return fitnessFunction;
    }
    
    @Override
    public abstract Individual clone();
    
    public abstract double getFitness();
    public abstract String getNumNodesAsString();
    public abstract String getTrainingFitnessAsString();
    public abstract String getTestFitnessAsString();  
    public abstract double[] getTrainingSemantics();
    public abstract double[] getTestSemantics();
}
