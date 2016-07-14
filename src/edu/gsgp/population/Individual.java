/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population;

import edu.gsgp.utils.Utils;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.nodes.Node;
import edu.gsgp.population.fitness.Fitness;
import java.math.BigInteger;

/**
 * 
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Individual implements Comparable<Individual>{
    protected Node tree;    
    protected Fitness fitnessFunction;

    public Individual(Node tree, Fitness fitnessFunction){
        this.tree = tree;
        this.fitnessFunction = fitnessFunction;
    }
    
    public Individual(Fitness fitnessFunction){
        this(null, fitnessFunction);
    }
    
    public Individual(Node tree, BigInteger numNodes, Fitness fitnessFunction) {
        this(tree, fitnessFunction);
        fitnessFunction.setNumNodes(numNodes);
    }
    
    public Individual(Node tree, int numNodes, Fitness fitnessFunction) {
        this(tree, new BigInteger(numNodes + ""), fitnessFunction);
    }
        
    public Individual(Node tree, Fitness fitnessFunction, ExperimentalData data) {
        this(tree, fitnessFunction);
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

    public void setTree(Node randomSubtree) {
        this.tree = randomSubtree;
    }
        
    public Fitness getFitnessFunction(){
        return fitnessFunction;
    }
    
    public double eval(double[] input){
        return tree.eval(input);
    }

    public void setNumNodes(BigInteger numNodes) {
        fitnessFunction.setNumNodes(numNodes);
    }
   
    public void startNumNodes() {
        fitnessFunction.setNumNodes(tree.getNumNodes());
    }
    
    
    @Override
    public Individual clone(){
        if(tree != null)
            return new Individual(tree.clone(null), fitnessFunction);
        return new Individual(fitnessFunction);
    }
    
    public boolean isBestSolution(double minError) {
        return getFitness() <= minError;
    }

    
    public String toString() {
        return tree.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    public String getNumNodesAsString() {
        return fitnessFunction.getNumNodes().toString();
    }

    
    public String getTrainingFitnessAsString() {
        return Utils.format(fitnessFunction.getTrainingFitness());
    }

    
    public String getTestFitnessAsString() {
        return Utils.format(fitnessFunction.getTestFitness());
    }
    
    public double getFitness() {
        double value = fitnessFunction.getComparableValue();
        if(Double.isInfinite(value) || Double.isNaN(value)) return Double.MAX_VALUE;
        return value;
    }
    
    public BigInteger getNumNodes() {
        return fitnessFunction.getNumNodes();
    }
    
    public double[] getTrainingSemantics() {
        return fitnessFunction.getSemantics(Utils.DatasetType.TRAINING);
    }

    
    public double[] getTestSemantics() {
        return fitnessFunction.getSemantics(Utils.DatasetType.TEST);
    }    
}
