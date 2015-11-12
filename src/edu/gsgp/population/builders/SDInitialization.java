/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.builders;

import edu.gsgp.population.GSGPIndividual;
import java.io.StringWriter;
import java.util.HashMap;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Semantic Driven Initialization. See "Beadle, L. Semantic and Structural 
 * Analysis of Genetic Programming Computing, University of Kent, 2009, 182-196"
 * for more details.
 * @author luiz
 */
public class SDInitialization {
    private HashMap<GSGPIndividual, String> simplifiedForms;
    
    private String simplify(GSGPIndividual individual){
        ExprEvaluator fEvaluator = new ExprEvaluator(false, 1);
        final StringWriter buf = new StringWriter();
        IExpr result = (IExpr) fEvaluator.evaluate(individual.toString());
        return result.toString();
    }
}


