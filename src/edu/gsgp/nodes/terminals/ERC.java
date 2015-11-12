/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.nodes.terminals;

import edu.gsgp.MersenneTwister;
import edu.gsgp.nodes.Node;

/**
 *
 * @author luiz
 */
public class ERC implements Terminal{
    private double value;
    private MersenneTwister rnd;
    private Node parent = null;
    private int parentArgPosition;

    public ERC(){}
    
    
    // Koza claimed to be generating from [-1.0, 1.0] but he wasn't,
    // given the published simple-lisp code.  It was [-1.0, 1.0).  This is
    // pretty minor, but we're going to go with the code rather than the
    // published specs in the books.  If you want to go with [-1.0, 1.0],
    // just change nextDouble() to nextDouble(true, true)
//    public void setup(MersenneTwister rnd) {
//        this.rnd = rnd;
//        this.value = this.rnd.nextDouble() * 2 - 1.0;
//    }
    
    @Override
    public int getArity() {
        return 0;
    }

    @Override
    public double eval(double[] inputs) {
        return value;
    }
    
    @Override
    public Terminal softClone(MersenneTwister rnd) {
        ERC newERC = new ERC();
        newERC.rnd = rnd;
        newERC.value = newERC.rnd.nextDouble() * 2 - 1.0;
        return newERC;
    }
    
    @Override
    public String toString() {
        return value + "";
    }
    
    @Override
    public Node getChild(int index) {
        return null;
    }

    @Override
    public int getNumNodes() {
        return 1;
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
        ERC newNode = new ERC();
        newNode.rnd = rnd;
        newNode.value = value;
        newNode.parent = parent;
        newNode.parentArgPosition = parentArgPosition;
        return newNode;
    }
}
