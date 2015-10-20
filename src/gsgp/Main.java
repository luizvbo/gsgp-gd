/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gsgp;

/**
 *
 * @author luiz
 */
public class Main {
    public static void main(String args[]){
        try{
            long startTime = System.currentTimeMillis();
            Experimenter experiment = new Experimenter(args);
            System.out.println("Elapsed Time: " + ((System.currentTimeMillis() - startTime)/1000) + " seconds");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
