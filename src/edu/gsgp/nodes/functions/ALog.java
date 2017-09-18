/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.nodes.functions;

import edu.gsgp.nodes.Node;

/**
 * Analytic Quotient
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class ALog extends Function{
    public ALog() { 
        super();
    }

    @Override
    public int getArity() { return 1; }
    
    @Override
    public double eval(double[] inputs) {
        double tmp = arguments[0].eval(inputs) * arguments[0].eval(inputs);
        return Math.log(Math.sqrt(1+tmp));
    }

    @Override
    public int getNumNodes() {
        return arguments[0].getNumNodes() + 1;
    }
    
    @Override
    public Function softClone() {
        return new ALog();
    }
    
    @Override
    public String toString() {
        return "ALog(" + arguments[0].toString() + ")";
    }
    
    @Override
    public Node clone(Node parent) {
        ALog newNode = new ALog();
        for(int i = 0; i < getArity(); i++) newNode.arguments[i] = arguments[i].clone(newNode);
        newNode.parent = parent;
        newNode.parentArgPosition = parentArgPosition;
        return newNode;
    }
}
