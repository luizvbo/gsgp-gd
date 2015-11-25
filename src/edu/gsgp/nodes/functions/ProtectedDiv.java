/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.nodes.functions;

import edu.gsgp.nodes.Node;

/**
 *
 * @author luiz
 */
public class ProtectedDiv extends Function{    
    public ProtectedDiv() {
        super();
    }

    @Override
    public int getArity() { return 2; }
    
    @Override
    public double eval(double[] inputs) {
        double den = arguments[1].eval(inputs);
        if(den == 0) return 1;
        return arguments[0].eval(inputs) / den;
    }

    @Override
    public int getNumNodes() {
        return arguments[0].getNumNodes() + arguments[1].getNumNodes() + 1;
    }
    
    @Override
    public Function softClone() {
        return new ProtectedDiv();
    }
    
    @Override
    public String toString() {
        return "(" + arguments[0].toString() + "%" + arguments[1].toString() + ")";
    }
    
    @Override
    public Node clone(Node parent) {
        ProtectedDiv newNode = new ProtectedDiv();
        for(int i = 0; i < getArity(); i++) newNode.arguments[i] = arguments[i].clone(newNode);
        newNode.parent = parent;
        newNode.parentArgPosition = parentArgPosition;
        return newNode;
    }
}
