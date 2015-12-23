/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.fitness;

import edu.gsgp.Utils.DatasetType;
import edu.gsgp.data.ExperimentalData;
import java.math.BigInteger;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public abstract class Fitness{
    protected double[] semanticsTr;
    protected double[] semanticsTs;
    
    protected BigInteger numNodes;
    
    

    public Fitness() {
        this(0);
    }
    
    public Fitness(int numNodes){
        this.numNodes = new BigInteger(numNodes + "");
    }
    
    /**
     * Return the semantics relative to the training or test data
     * @param dataType What semantics to return (relative to training or test)
     * @return The semantics
     */
    public final double[] getSemantics(DatasetType dataType){
        if(dataType == DatasetType.TRAINING)
            return semanticsTr;
        else
            return semanticsTs;
    }
    
    /**
     * Set the the semantics relative to the training or test data
     * @param semantics The new semantics
     * @param dataType What semantics to set (relative to training or test)
     */
    public final void setSemantics(double[] semantics, DatasetType dataType) {
        if(dataType == DatasetType.TRAINING)
            semanticsTr = semantics;
        else
            semanticsTs = semantics;
    }
    
    /**
     * Set the the semantics with an empty array with the size given. 
     * @param size The size of the new array
     * @param dataType What semantics to set (training or test)
     */
    public final void setSemantics(int size, DatasetType dataType) {
        if(dataType == DatasetType.TRAINING)
            semanticsTr = new double[size];
        else
            semanticsTs = new double[size];
    }

    /**
     * Return the number of nodes as a BigInteger. 
     * @return Number of nodes
     */
    public final BigInteger getNumNodes() {
        return numNodes;
    }

    /**
     * Set the number of nodes.
     * @param numNodes Number of nodes (BigInteger)
     */
    public final void setNumNodes(BigInteger numNodes) {
        this.numNodes = numNodes;
    }
    
    /**
     * Set the number of nodes, by converting an int in BigNumber.
     * @param numNodes Number of nodes
     */
    public final void setNumNodes(int numNodes) {
        this.numNodes = new BigInteger(numNodes + "");
    }
    
    /**
     * Add an int to the number of nodes
     * @param moreNodes Number of nodes to be added
     */
    public final void addNumNodes(int moreNodes){
        this.numNodes.add(new BigInteger(moreNodes+""));
    }
    
    /**
     * Add a BigInteger to the number of nodes
     * @param moreNodes Number of nodes to be added
     */
    public final void addNumNodes(BigInteger moreNodes){
        this.numNodes.add(moreNodes);
    }
    
    /**
     * Befor starting to compute the fitness function, its variables must be 
     * (re)initialized.
     * @param dataType Inidicate if the fitness refer to training or test set
     * @param datasets Training and test data in an ExperimentalData object
     */
    public abstract void resetFitness(DatasetType dataType, ExperimentalData datasets);
    public abstract void setSemanticsAtIndex(double estimated, double desired, int index, DatasetType dataType);
    public abstract void computeFitness(DatasetType dataType);
    
    public abstract Fitness softClone();
    public abstract double getTrainingFitness();
    public abstract double getTestFitness();
    public abstract double getComparableValue();
}
