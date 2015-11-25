/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population;

import edu.gsgp.Utils;
import java.math.BigInteger;
import edu.gsgp.nodes.Node;
import edu.gsgp.population.fitness.Fitness;

/**
 *
 * @author luiz
 */
public class GSGPIndividual extends Individual{
        
    public GSGPIndividual(Node tree, Fitness fitnessFunction){
        super(tree, fitnessFunction);
    }
    
    public GSGPIndividual(Fitness fitnessFunction){
        super(null, fitnessFunction);
    }
    
    public GSGPIndividual(Node tree, BigInteger numNodes, Fitness fitnessFunction) {
        super(tree, fitnessFunction);
        fitnessFunction.setNumNodes(numNodes);
    }
    
    public GSGPIndividual(Node tree, int numNodes, Fitness fitnessFunction) {
        this(tree, new BigInteger(numNodes + ""), fitnessFunction);
    }
    
    public GSGPIndividual(BigInteger numNodes, Fitness fitnessFunction) {
        this(null, numNodes, fitnessFunction);
    }
    
    public double eval(double[] input){
        return tree.eval(input);
    }

    public BigInteger getNumNodes() {
        return fitnessFunction.getNumNodes();
    }

    public void setNumNodes(BigInteger numNodes) {
        fitnessFunction.setNumNodes(numNodes);
    }
   
    public void startNumNodes() {
        fitnessFunction.setNumNodes(tree.getNumNodes());
    }
    
//    @Override
//    public int compareTo(Individual o) {
//        if (getFitness() < o.getFitness()){
//            return -1;
//        }
//        if (getFitness() > o.getFitness()) {
//            return 1;
//        }
//        return 0;
//    }
    
    public GSGPIndividual clone(){
        if(tree != null)
            return new GSGPIndividual(tree.clone(null), fitnessFunction);
        return new GSGPIndividual(fitnessFunction);
    }

    @Override
    public boolean isBestSolution(double minError) {
        return getFitness() <= minError;
    }

    @Override
    public String toString() {
        return tree.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTree(Node randomSubtree) {
        this.tree = randomSubtree;
    }

    @Override
    public String getNumNodesAsString() {
        return fitnessFunction.getNumNodes().toString();
    }

    @Override
    public String getTrainingFitnessAsString() {
        return df.format(fitnessFunction.getTrainingFitness());
    }

    @Override
    public String getTestFitnessAsString() {
        return df.format(fitnessFunction.getTestFitness());
    }

    @Override
    public double getFitness() {
        return fitnessFunction.getComparableValue();
    }

    @Override
    public double[] getTrainingSemantics() {
        return fitnessFunction.getSemantics(Utils.DataType.TRAINING);
    }

    @Override
    public double[] getTestSemantics() {
        return fitnessFunction.getSemantics(Utils.DataType.TEST);
    }
}
