/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.nodes;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public interface Node {
    public int getArity();
    
    public double eval(double[] inputs);
    
    public int getNumNodes();
    
    public Node clone(Node parent);
    
    public Node getChild(int index);
    
    public Node getParent();
    
    public void setParent(Node parent, int argPosition);
    
    public int getParentArgPosition();    
}
