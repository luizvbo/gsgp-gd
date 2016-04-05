/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.populator;

import edu.gsgp.MersenneTwister;
import edu.gsgp.Utils.DatasetType;
import edu.gsgp.data.Dataset;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.Instance;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.nodes.Node;
import edu.gsgp.nodes.functions.Function;
import edu.gsgp.nodes.terminals.Input;
import edu.gsgp.nodes.terminals.Terminal;
import edu.gsgp.population.GSGPIndividual;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;
import edu.gsgp.population.fitness.Fitness;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 *
 * Semantic Driven Initialization (SDI). See "Beadle, L. Semantic and Structural 
 * Analysis of Genetic Programming Computing, University of Kent, 2009, 182-196"
 * for more details.
 * @author luiz
 */
public class SDIPopulator extends Populator{
    public SDIPopulator(PropertiesManager properties) {
        super(properties);
    }
    
    /**
     * Evaluate the new individuals
     * @param ind Individual to be evaluated
     * @return The computed fitness function w.r.t. the evaluated individual
     */
    private Fitness evaluate(Node newTree, ExperimentalData expData){
        Fitness fitnessFunction = properties.geFitnessFunction();
        for(DatasetType dataType : DatasetType.values()){
            // Compute the (training/test) semantics of generated random tree
            fitnessFunction.resetFitness(dataType, expData);
            Dataset dataset = expData.getDataset(dataType);
            int instanceIndex = 0;
            for (Instance instance : dataset) {
                double estimated = newTree.eval(instance.input);
                fitnessFunction.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, dataType);
            }
            fitnessFunction.computeFitness(dataType);
        }
        return fitnessFunction;
    }

    /**
     * Create a new population
     * @param rndGenerator Pseudorandom number generator
     * @param size The size of the population to be generated
     * @return The generated popualtion
     */
    @Override
    public Population populate(MersenneTwister rndGenerator, ExperimentalData expData, int size) {        
        Population population = new Population();
        Terminal[] terminalSet = properties.getTerminalSet(rndGenerator);
        // Initialize pop with all the Inputs
        for(Terminal t : terminalSet){
            if(t instanceof Input){
                Node newTree = t.softClone(rndGenerator);
                Fitness fitnessFunction = evaluate(newTree, expData);
                GSGPIndividual newIndividual = new GSGPIndividual(newTree, fitnessFunction);
                // Add the new inidividual to the list of building blocks
                population.add(newIndividual);
            }
        }
        int numAttempts = 0;
        Function candidateFunc = null;
        Fitness candidateFuncFitness = null;
        // Add new individuals until the population is complete
        while(population.size() < properties.getPopulationSize()){
            // Select a random function from the function set
            Function func = properties.getRandomFunction(rndGenerator);
            for(int i = 0; i < func.getArity(); i++){
                // Randomly selects a tree from buildingBlocks
                int rndIndIndex = rndGenerator.nextInt(population.size());
                Node newTree = population.get(rndIndIndex).getTree().clone(func);
                // Add in the selected function
                func.addNode(newTree, i);
            }
            // Evaluate the new function
            Fitness fitnessFunction = evaluate(func, expData);
            // Keep the new function in memory, if this is the first attemp to add
            if(numAttempts == 0){
                candidateFunc = func;
                candidateFuncFitness = fitnessFunction;
            }
            numAttempts++;
            
            // Is the new function a constant?
            boolean isConstant = isConstantFunction(fitnessFunction);
            // Is there a similar semantics in the population?
            boolean similarSemantics = false;
            if(!isConstant){
                for(Individual ind : population){
                    if(ind.getFitness() == fitnessFunction.getComparableValue()){
                        similarSemantics = true;
                        break;
                    }
                }
                // If there is no inidividual with similar semantics, add in the hash map
                if(!similarSemantics){
                    GSGPIndividual newIndividual = new GSGPIndividual(func, fitnessFunction);
                    // Add the new inidividual to the list of building blocks
                    population.add(newIndividual);
                    // Reset the counter 
                    numAttempts = 0;

                    System.out.println(population.size());
                }
            }
            // If the maximum number o attempts to generate a different individual was
            // reached, add the original one anyway
            if(numAttempts >= properties.getMaxInitAttempts()){
                GSGPIndividual newIndividual = new GSGPIndividual(candidateFunc, candidateFuncFitness);
                // Add the new inidividual to the list of building blocks
                population.add(newIndividual);
                // Reset the counter 
                numAttempts = 0;
            }            
        }
        return population;
    }

    /**
     * Check if the semantics of a function is a constant (all values are equal).
     * @param fitness The fitness computed w.r.t. the function
     * @return True if it is constant and false otherwise
     */
    private boolean isConstantFunction(Fitness fitness) {
        double semantics[] = fitness.getSemantics(DatasetType.TRAINING);
        for(int i = 1; i < semantics.length; i++){
            if(semantics[i-1] != semantics[i])
                return false;
        }
        return true;
    }

    @Override
    public Populator softClone() {
        return new SDIPopulator(properties);
    }
}


