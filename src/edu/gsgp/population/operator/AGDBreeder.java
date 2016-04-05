/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.operator;

import edu.gsgp.MersenneTwister;
import edu.gsgp.Utils.DatasetType;
import edu.gsgp.data.Dataset;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.Instance;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.population.GSGPIndividual;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;
import edu.gsgp.population.fitness.Fitness;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Additive Geometric Dispersion Operator
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20016, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class AGDBreeder extends MGDBreeder{
        
    public AGDBreeder(PropertiesManager properties, Double probability) {
        super(properties, probability);
    }
    
    @Override
    public Breeder softClone(PropertiesManager properties) {
        return new AGDBreeder(properties, this.probability);
    }


    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator, ExperimentalData expData, GSGPIndividual p) {
        ArrayList<Bound> bounds = new ArrayList<>();
        // UpperBound: alpha <= ub
        // LowerBound: lb <= alpha
        int numUpperBounds = 0;
        Dataset trainingData = expData.getDataset(DatasetType.TRAINING);
        // Compute the restrictions regarding alpha to put the inidividual in the correct position
        for(int i = 0; i < trainingData.size(); i++){
            double pSem = p.getTrainingSemantics()[i];
            double tSem = trainingData.get(i).output;
            // The semantic value for this dimension should be smaller than the target
            if(numIndGreaterTarget[i] > numIndLessTarget[i]){
                // This is an upper bound 
                bounds.add(new Bound(tSem-pSem, Bound.upper));
                numUpperBounds++;
            }

            // The semantic value for this dimension should be greater than the target
            if(numIndGreaterTarget[i] < numIndLessTarget[i]){
                // This is a lower bound
                bounds.add(new Bound(tSem-pSem, Bound.lower));
            }
            
        }
        Collections.sort(bounds);
        int maxCoverage = numUpperBounds;
        int currentCoverage = numUpperBounds;
        int maxCovIndex = -1;
        for(int i = 0; i < bounds.size(); i++){
            Bound bound = bounds.get(i);
            if(bound.isLowerBound()){
                currentCoverage++;
                if(currentCoverage > maxCoverage){
                    maxCoverage = currentCoverage;
                    maxCovIndex = i;
                }
            }
            else{
                currentCoverage--;
            }
        }
        double alpha;
        if(maxCovIndex == -1){
            if(bounds.isEmpty())
                alpha = 1;
            else
                alpha = bounds.get(0).value - 1;
        }
        else if(maxCovIndex == bounds.size()-1)
            alpha = bounds.get(maxCovIndex).value + 1;
        else 
            alpha = bounds.get(maxCovIndex).value + 
                    (bounds.get(maxCovIndex+1).value - bounds.get(maxCovIndex).value) * rndGenerator.nextDouble();
        Fitness fitnessFunction = evaluate(p, alpha, expData);
        BigInteger numNodes = p.getNumNodes().add(new BigInteger(""+2));
        GSGPIndividual offspring = new GSGPIndividual(numNodes, fitnessFunction);
        return offspring;
    }
    
    private Fitness evaluate(GSGPIndividual ind, double alpha, ExperimentalData expData){
        Fitness fitnessFunction = ind.getFitnessFunction().softClone();
        for(DatasetType dataType : DatasetType.values()){
            // Compute the (training/test) semantics of generated random tree
            fitnessFunction.resetFitness(dataType, expData);
            Dataset dataset = expData.getDataset(dataType);
            double[] semInd;
            if(dataType == DatasetType.TRAINING)
                semInd = ind.getTrainingSemantics();
            else 
                semInd =  ind.getTestSemantics();
            int instanceIndex = 0;
            for (Instance instance : dataset) {
                double estimated = semInd[instanceIndex] + alpha;
                fitnessFunction.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, dataType);
            }
            fitnessFunction.computeFitness(dataType);
        }
        return fitnessFunction;
    }
}
