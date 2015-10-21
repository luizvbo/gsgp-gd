/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gsgp.nodes.functions;

import gsgp.nodes.Node;

/**
 *
 * @author luiz
 */
public class GSXover implements Function{
    private final Node[] arguments;
    private Node parent = null;
    private int parentArgPosition;  
    
    private final int arity = 3;

    public GSXover() {
        arguments = new Node[arity];
    }
    
    @Override
    public int getArity(){ return arity; }

    @Override
    public double eval(double[] inputs) {
        double tr = arguments[0].eval(inputs);
        return tr*arguments[1].eval(inputs) + (1-tr)*arguments[2].eval(inputs);
    }
    
    @Override
    public int getNumNodes() {
        return arguments[0].getNumNodes() + arguments[1].getNumNodes() + 1;
    }

    @Override
    public Node softClone() {
        return new GSXover();
    }
    
    @Override
    public void addNode(Node newNode, int argPosition) {
        arguments[argPosition] = newNode;
        newNode.setParent(this, argPosition);
    }

    @Override
    public String toString() {
        return "SGX(" + arguments[0] + "," + arguments[1] + "," + arguments[2] + ")";
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
        GSXover newNode = new GSXover();
        for(int i = 0; i < arity; i++) newNode.arguments[i] = arguments[i].clone(newNode); 
        newNode.parent = parent;
        newNode.parentArgPosition = parentArgPosition;
        return newNode;
    }
}
