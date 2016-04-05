/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.operator;

import edu.gsgp.MersenneTwister;
import edu.gsgp.Utils;
import edu.gsgp.Utils.DatasetType;
import edu.gsgp.data.Dataset;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.Instance;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.nodes.Node;
import edu.gsgp.population.GSGPIndividual;
import edu.gsgp.population.Individual;
import edu.gsgp.population.fitness.Fitness;
import java.math.BigInteger;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class GSXBreeder extends Breeder{

    public GSXBreeder(PropertiesManager properties, Double probability) {
        super(properties, probability);
    }
    
    private Fitness evaluate(GSGPIndividual ind1,
                            GSGPIndividual ind2, 
                            Node randomTree, 
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
            for (Instance instance : dataset) {
                double rtValue = Utils.sigmoid(randomTree.eval(instance.input));
//                double estimated = rtValue*ind1.getTrainingSemantics()[instanceIndex] + (1-rtValue)*ind2.getTrainingSemantics()[instanceIndex];
                double estimated = rtValue*semInd1[instanceIndex] + (1-rtValue)*semInd2[instanceIndex];
                fitnessFunction.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, dataType);
            }
            fitnessFunction.computeFitness(dataType);
        }
        return fitnessFunction;
    }

    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator, ExperimentalData expData) {
        GSGPIndividual p1 = (GSGPIndividual)properties.selectIndividual(originalPopulation, rndGenerator);
        GSGPIndividual p2 = (GSGPIndividual)properties.selectIndividual(originalPopulation, rndGenerator);
        while(p1.equals(p2)) p2 = (GSGPIndividual)properties.selectIndividual(originalPopulation, rndGenerator);
        Node rt = properties.getRandomTree(rndGenerator);
        BigInteger numNodes = p1.getNumNodes().add(p2.getNumNodes()).add(new BigInteger(rt.getNumNodes() + "")).add(BigInteger.ONE);
        Fitness fitnessFunction = evaluate(p1, p2, rt, expData);
        GSGPIndividual offspring = new GSGPIndividual(numNodes, fitnessFunction);
        return offspring;
    }

    @Override
    public Breeder softClone(PropertiesManager properties) {
        return new GSXBreeder(properties, probability);
    }
}
