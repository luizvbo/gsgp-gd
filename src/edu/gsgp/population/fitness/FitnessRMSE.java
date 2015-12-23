/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.fitness;

import edu.gsgp.Utils.DatasetType;
import edu.gsgp.data.ExperimentalData;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class FitnessRMSE extends Fitness{    
    private double rmseTr;
    private double rmseTs;

    public FitnessRMSE(int numNodes) {
        super(numNodes);
    }

    public FitnessRMSE() {
        super(0);
    }
    
    public void setRMSE(double rmse, DatasetType dataType) {
        if(dataType == DatasetType.TRAINING)
            rmseTr = rmse;
        else
            rmseTs = rmse;
    }

    public double getRMSE(DatasetType dataType){
        if(dataType == DatasetType.TRAINING)
            return rmseTr;
        return rmseTs;
    }
    
    /** Control variables used during fitness calculation. **/
    // Variable to store the sum of squared errors (to compute the RMSE).
    private double ctrSumSquarError;
    // Variable to indicate What fitness we are computing.
//    private DataType ctrFitnessType;
    
    @Override
    public void resetFitness(DatasetType dataType, ExperimentalData datasets){
        ctrSumSquarError = 0;
        setSemantics(datasets.getDataset(dataType).size(), dataType);
    }
    
    @Override
    public void setSemanticsAtIndex(double estimated, double desired, int index, DatasetType dataType){
        getSemantics(dataType)[index] = estimated;
        double error = estimated - desired;
        ctrSumSquarError += error * error;
    }
    
    @Override
    public void computeFitness(DatasetType dataType){
        double rmse = Math.sqrt(ctrSumSquarError/getSemantics(dataType).length);
        setRMSE(rmse, dataType);
    }

    @Override
    public Fitness softClone() {
        return new FitnessRMSE();
    }

    @Override
    public double getTrainingFitness() {
        return rmseTr;
    }

    @Override
    public double getTestFitness(){
        return rmseTs;
    }
    
    @Override
    public double getComparableValue() {
        return getRMSE(DatasetType.TRAINING);
    }
}