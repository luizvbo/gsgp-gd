/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gsgp.data;

/**
 *
 * @author luiz
 */
public class ExperimentDataset {
    public Dataset training;
    public Dataset test;

    public ExperimentDataset() {
        training = new Dataset();
        test = new Dataset();
    }

    public ExperimentDataset(Dataset training, Dataset test) {
        this.training = training;
        this.test = test;
    }    
}
