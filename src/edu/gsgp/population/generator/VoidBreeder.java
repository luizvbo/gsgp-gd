/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.generator;

import edu.gsgp.MersenneTwister;
import edu.gsgp.Utils.DataType;
import edu.gsgp.data.Dataset;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.Instance;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.nodes.Node;
import edu.gsgp.population.GSGPIndividual;
import edu.gsgp.population.Individual;
import edu.gsgp.population.fitness.Fitness;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class VoidBreeder extends Breeder{
    private ExperimentalData experimentalData;
    
    public VoidBreeder(ExperimentalData experimentalData, PropertiesManager properties) {
        super(1, properties, null);
        this.experimentalData = experimentalData;
    }

    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator) {
        Node newTree = properties.getNewIndividualTree(rndGenerator);
        Fitness fitnessFunction = evaluate(newTree, properties.geFitness());
        return new GSGPIndividual(newTree, newTree.getNumNodes(), fitnessFunction);
    }
    
    private Fitness evaluate(Node newTree,
                             Fitness fitnessFunction){
        for(DataType dataType : DataType.values()){
            // Compute the (training/test) semantics of generated random tree
            fitnessFunction.resetFitness(dataType, experimentalData);
            Dataset dataset = experimentalData.getDataset(dataType);
            int instanceIndex = 0;
            for (Instance instance : dataset) {
                double estimated = newTree.eval(instance.input);
                fitnessFunction.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, dataType);
            }
            fitnessFunction.computeFitness(dataType);
        }
        return fitnessFunction;
    }
}
