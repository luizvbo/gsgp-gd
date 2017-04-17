/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.operator;

import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.utils.Utils.DatasetType;
import edu.gsgp.experiment.data.Dataset;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.experiment.data.Instance;
import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.population.Individual;
import edu.gsgp.population.fitness.Fitness;
import java.math.BigInteger;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class GSXEBreeder extends Breeder{

    public GSXEBreeder(PropertiesManager properties, Double probability) {
        super(properties, probability);
    }
    
    private Fitness evaluate(Individual ind1,
                             Individual ind2, 
                            double r, 
                            ExperimentalData expData){
        Fitness fitnessFunction = ind1.getFitnessFunction().softClone();
        for(DatasetType dataType : DatasetType.values()){
            // Compute the (training/test) semantics of generated random tree
            fitnessFunction.resetFitness(dataType, expData);
            Dataset dataset = expData.getDataset(dataType);
            double[] semInd1;
            double[] semInd2;
            if(dataType == DatasetType.TRAINING){
                semInd1 = ind1.getTrainingSemantics();
                semInd2 = ind2.getTrainingSemantics();
            }
            else{
                semInd1 = ind1.getTestSemantics();
                semInd2 = ind2.getTestSemantics();
            }
            int instanceIndex = 0;
            double oneMinusR = 1-r;
            for (Instance instance : dataset) {
                double estimated = r*semInd1[instanceIndex] + oneMinusR*semInd2[instanceIndex];
                fitnessFunction.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, dataType);
            }
            fitnessFunction.computeFitness(dataType);
        }
        return fitnessFunction;
    }

    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator, ExperimentalData expData) {
        Individual p1 = (Individual)properties.selectIndividual(originalPopulation, rndGenerator);
        Individual p2 = (Individual)properties.selectIndividual(originalPopulation, rndGenerator);
        while(p1.equals(p2)) p2 = (Individual)properties.selectIndividual(originalPopulation, rndGenerator);
        double r = rndGenerator.nextDouble();
        
        BigInteger numNodes = p1.getNumNodes().add(p2.getNumNodes()).add(BigInteger.ONE);
//        BigInteger numNodes = BigInteger.ONE;
        
        Fitness fitnessFunction = evaluate(p1, p2, r, expData);
        Individual offspring = new Individual(null, numNodes, fitnessFunction);
        return offspring;
    }

    @Override
    public Breeder softClone(PropertiesManager properties) {
        return new GSXEBreeder(properties, probability);
    }
}
