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
public interface Function extends Node{
    public void addNode(Node newNode, int arPosition);
}
