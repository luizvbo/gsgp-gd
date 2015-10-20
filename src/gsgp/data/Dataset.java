/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gsgp.data;

import java.util.ArrayList;
import gsgp.Utils;

/**
 * Dataset.java
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Dataset extends ArrayList<Instance>{   
    private double[] outputs;
    // Used to calculate the coefficient of determination
    private Double ss_total = null;
    
    /**
     * Constructor declaration
     */
    public Dataset() {
        super();
    }

    /**
     * Constructor declaration
     * @param dataset Dataset copied to the new one.
     */
    public Dataset(Dataset dataset) {
        super(dataset);
    }
    
    /**
     * Creates a new instance and add it to the dataset.
     * @param input Instance input
     * @param output Instance output
     */
    public void add(double[] input, Double output){
        Instance newInstance = new Instance(input, output);
        this.add(newInstance);
    }
    
    /**
     * Soft clones this object, copying the references to data instances to a 
     * new ArrayList. 
     * @return A Dataset pointing to the instances of this one.
     */
    public Dataset softClone() {
        Dataset newDataset = new Dataset();
        newDataset.addAll(this);
        return newDataset;
    }
    
    /**
     * Return the number of inputs of the dataset.
     * @return The number of inputs.
     */
    public int getInputNumber(){
        return get(0).input.length;
    }

    @Override
    public String toString() {
        return size() + ""; 
    }

    public double getOutputSD() {
        double mean = 0;
        double[] outputs = new double[size()];
        for(int i = 0; i < size(); i ++){
            double tmp = get(i).output;
            outputs[i] = tmp;
            mean += tmp;
        }
        return Utils.getSD(outputs, mean/size());
    }

    public double[] getOutputs() {
        if(outputs == null){
            setOutputs();
        }
        return outputs;
    }
    
    private void setOutputs(){
        outputs = new double[size()];
        for(int i = 0; i < size(); i++){
            outputs[i] = get(i).output;
        }
    }
    
    public double getSStotal(){
        if(ss_total == null){
            double meanOutput = 0;
            ss_total = new Double(0);
            if(outputs == null) setOutputs();
            for(int i = 0; i < outputs.length; i++) meanOutput += outputs[i];
            meanOutput /= outputs.length;            
            for(int i = 0; i < outputs.length; i++){
                double tmp = outputs[i] - meanOutput;
                ss_total += tmp * tmp;
            }
        }
        return ss_total;
    }
}

