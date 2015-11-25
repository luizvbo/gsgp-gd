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
 *
 * @author luiz
 */
public class GSXBreeder extends Breeder{
    private ExperimentalData experimentalData;

    public GSXBreeder(ExperimentalData experimentalData,
                      double probability, 
                      PropertiesManager properties, 
                      Population population) {
        super(probability, properties, population);
        this.experimentalData = experimentalData;
    }
    
    private Fitness evaluate(GSGPIndividual ind1,
                            GSGPIndividual ind2, 
                            Node randomTree){
        Fitness fitnessFunction = ind1.getFitnessFunction().softClone();
        for(DataType dataType : DataType.values()){
            // Compute the (training/test) semantics of generated random tree
            fitnessFunction.resetFitness(dataType, experimentalData);
            Dataset dataset = experimentalData.getDataset(dataType);
            int instanceIndex = 0;
            for (Instance instance : dataset) {
                double rtValue = Utils.sigmoid(randomTree.eval(instance.input));
                double estimated = rtValue*ind1.getTrainingSemantics()[instanceIndex] + (1-rtValue)*ind2.getTrainingSemantics()[instanceIndex];
                fitnessFunction.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, dataType);
            }
            fitnessFunction.computeFitness(dataType);
        }
        return fitnessFunction;
    }

    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator) {
        GSGPIndividual p1 = (GSGPIndividual)properties.selectIndividual(population, rndGenerator);
        GSGPIndividual p2 = (GSGPIndividual)properties.selectIndividual(population, rndGenerator);
        while(p1.equals(p2)) p2 = (GSGPIndividual)properties.selectIndividual(population, rndGenerator);
        Node rt = properties.getRandomTree(rndGenerator);
        BigInteger numNodes = p1.getNumNodes().add(p2.getNumNodes()).add(new BigInteger(rt.getNumNodes() + "")).add(BigInteger.ONE);
        Fitness fitnessFunction = evaluate(p1, p2, rt);
        GSGPIndividual offspring = new GSGPIndividual(numNodes, fitnessFunction);
        return offspring;
    }
}
