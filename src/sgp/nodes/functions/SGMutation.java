/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sgp.nodes.functions;

import sgp.Utils;
import sgp.nodes.Node;

/**
 *
 * @author luiz
 */
public class SGMutation implements Function{
    private final Node[] arguments;
    private double ms;
    private Node parent = null;
    private int parentArgPosition;
    
    private final int arity = 3;
    
    public SGMutation(double ms) {
        arguments = new Node[arity];
        this.ms = ms;
    }
    
    @Override
    public int getArity(){ return arity; }

    @Override
    public double eval(double[] inputs) {
        return arguments[2].eval(inputs) + ms * (Utils.sigmoid(arguments[0].eval(inputs)) - Utils.sigmoid(arguments[1].eval(inputs)));
    }

    @Override
    public int getNumNodes() {
        return arguments[0].getNumNodes() + arguments[1].getNumNodes() + arguments[2].getNumNodes() + 1;
    }

    @Override
    public Node softClone() {
        return new SGMutation(ms);
    }

    @Override
    public void addNode(Node newNode, int argPosition) {
        arguments[argPosition] = newNode;
        newNode.setParent(this, argPosition);
    }
    
    @Override
    public String toString() {
        return "SGM(" + ms + "," + arguments[0] + "," + arguments[1] + "," + arguments[2] + ")";
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
        SGMutation newNode = new SGMutation(ms);
        for(int i = 0; i < arity; i++) newNode.arguments[i] = arguments[i].clone(newNode);
        newNode.parent = parent;
        newNode.ms = ms;
        newNode.parentArgPosition = parentArgPosition;
        return newNode;
    }
}
