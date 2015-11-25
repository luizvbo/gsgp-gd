/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.builders;

import edu.gsgp.MersenneTwister;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.nodes.functions.Function;
import edu.gsgp.nodes.terminals.Terminal;
import edu.gsgp.population.GSGPIndividual;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;
import edu.gsgp.population.fitness.Fitness;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 *
 * Semantic Driven Initialization. See "Beadle, L. Semantic and Structural 
 * Analysis of Genetic Programming Computing, University of Kent, 2009, 182-196"
 * for more details.
 * @author luiz
 */
public class SDInitialization {
    /** Hash to store the simplified versions of the individuals **/
    private HashMap<GSGPIndividual, String> simplifiedForms;
    
    /** The largest maximum tree depth RAMPED HALF-AND-HALF can specify. */
    protected final int maxDepth;

    /** The smallest maximum tree depth RAMPED HALF-AND-HALF can specify. */
    protected final int minDepth;

    /** GP function set. **/
    protected final Function[] functions;
            
    /** GP terminal set. **/
    protected final Terminal[] terminals;
    
    /** Pseudorandom number generator. **/
    protected final MersenneTwister rnd;
    
    /** Size of the population to be generated. **/
    protected final int populationSize;
    
    protected final Fitness fitnessFunction;

    public SDInitialization(final int maxDepth, 
                            final int minDepth, 
                            final Function[] functions,
                            final Terminal[] terminals,
                            final MersenneTwister rnd,
                            final int populationSize,
                            final Fitness fitnessFunction) {
        this.maxDepth = maxDepth;
        this.minDepth = minDepth;
        this.functions = functions;
        this.terminals = terminals;
        this.rnd = rnd;
        this.populationSize = populationSize;
        this.fitnessFunction = fitnessFunction;
    }
    
    public Population getNewPopulation(ExperimentalData dataset){
        ArrayList<Individual> buildingBlocks = new ArrayList<Individual>();
        // Initialize pop with all the terminals
        for(Terminal t : terminals){
            buildingBlocks.add(new GSGPIndividual(t.softClone(rnd), fitnessFunction.softClone()));
        }
        // Add new individuals until the population is complete
        while(buildingBlocks.size() < populationSize){
            Function f = functions[rnd.nextInt(functions.length)].softClone();
            for(int i = 0; i < f.getArity(); i++){
                f.addNode(buildingBlocks.get(rnd.nextInt(buildingBlocks.size())).getTree().clone(f), i);
            }
            
        }
        return new Population(buildingBlocks);
    }
    
    
            
    private String simplify(GSGPIndividual individual){
        ExprEvaluator fEvaluator = new ExprEvaluator(false, 1);
        final StringWriter buf = new StringWriter();
        IExpr result = (IExpr) fEvaluator.evaluate(individual.toString());
        return result.toString();
    }
}


