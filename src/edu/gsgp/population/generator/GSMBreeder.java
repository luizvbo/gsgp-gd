/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.generator;

import edu.gsgp.MersenneTwister;
import edu.gsgp.Utils;
import edu.gsgp.Utils.DataType;
import edu.gsgp.data.Dataset;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.Instance;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.nodes.Node;
import edu.gsgp.population.GSGPIndividual;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;
import edu.gsgp.population.fitness.Fitness;
import java.math.BigInteger;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class GSMBreeder extends Breeder{
    private double mutationStep;
    private ExperimentalData experimentalData;

    public GSMBreeder(ExperimentalData experimentalData, double mutationStep, double probability, PropertiesManager properties, Population population) {
        super(probability, properties, population);
        this.experimentalData = experimentalData;
        this.mutationStep = mutationStep;
    }
    
    private Fitness evaluate(GSGPIndividual ind, 
                             Node randomTree1,
                             Node randomTree2){
        Fitness fitnessFunicon = ind.getFitnessFunction().softClone();
        for(DataType dataType : DataType.values()){
            // Compute the (training/test) semantics of generated random tree
            fitnessFunicon.resetFitness(dataType, experimentalData);
            Dataset dataset = experimentalData.getDataset(dataType);
            int instanceIndex = 0;
            for (Instance instance : dataset) {
                double rtValue = Utils.sigmoid(randomTree1.eval(instance.input));
                rtValue -= Utils.sigmoid(randomTree2.eval(instance.input));
                double estimated = ind.getTrainingSemantics()[instanceIndex] + mutationStep * rtValue;
                fitnessFunicon.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, dataType);
            }
            fitnessFunicon.computeFitness(dataType);
        }
        return fitnessFunicon;
    }

    public void setMutationStep(double mutationStep) {
        this.mutationStep = mutationStep;
    }

    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator) {
        GSGPIndividual p = (GSGPIndividual)properties.selectIndividual(population, rndGenerator);
        Node rt1 = properties.getRandomTree(rndGenerator);
        Node rt2 = properties.getRandomTree(rndGenerator);
        BigInteger numNodes = p.getNumNodes().add(new BigInteger(rt1.getNumNodes()+"")).
                                              add(new BigInteger(rt2.getNumNodes()+"")).
                                              add(BigInteger.ONE);
        Fitness fitnessFunction = evaluate(p, rt1, rt2);
        GSGPIndividual offspring = new GSGPIndividual(numNodes, fitnessFunction);
        return offspring;
    }
}
