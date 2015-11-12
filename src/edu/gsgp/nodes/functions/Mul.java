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
public class Mul implements Function{
    private Node[] arguments;
    private Node parent = null;
    private int parentArgPosition;  
    
    private final int arity = 2;

    public Mul() {
        arguments = new Node[arity];
    }
    
    @Override
    public final int getArity(){ return arity; }

    @Override
    public double eval(double[] inputs) {
        return arguments[0].eval(inputs) * arguments[1].eval(inputs);
    }

    @Override
    public int getNumNodes() {
        return arguments[0].getNumNodes() + arguments[1].getNumNodes() + 1;
    }
    
    @Override
    public Function softClone() {
        return new Mul();
    }

    @Override
    public void addNode(Node newNode, int argPosition) {
        arguments[argPosition] = newNode;
        newNode.setParent(this, argPosition);
    }
    
    @Override
    public String toString() {
        return "(" + arguments[0].toString() + "*" + arguments[1].toString() + ")";
    }
    
    @Override
    public Node getChild(int index) {
        return arguments[index];
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public void setParent(Node parent, int argPosition) {
        this.parent = parent;
        this.parentArgPosition = argPosition;
    }

    @Override
    public int getParentArgPosition() {
        return parentArgPosition;
    }
    
    @Override
    public Node clone(Node parent) {
        Mul newNode = new Mul();
        for(int i = 0; i < arity; i++) newNode.arguments[i] = arguments[i].clone(newNode);
        newNode.parent = parent;
        newNode.parentArgPosition = parentArgPosition;
        return newNode;
    }
}
