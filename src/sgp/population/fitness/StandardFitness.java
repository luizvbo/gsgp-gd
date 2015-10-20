/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sgp.population.fitness;

import sgp.Utils.TargetType;

/**
 *
 * @author luiz
 */
public class StandardFitness implements Fitness{
    private long numNodes;
    private double[] tr_semantics;
    private double tr_rmse;
    private double[] ts_semantics;
    private double ts_rmse;
    
//    private static ExperimentDataset data;
//
//    public static void setData(ExperimentDataset data){
//        StandardFitness.data = data;
//    }
    
    public StandardFitness(long numNodes) {
        this.numNodes = numNodes;
//        tr_semantics = new double[data.training.size()];
//        ts_semantics = new double[data.test.size()];
    }

    public void setNumNodes(long numNodes) {
        this.numNodes = numNodes;
    }

    public long getNumNodes() {
        return numNodes;
    }
    
    public double[] getSemantics(TargetType target){
        if(target == TargetType.TRAINING)
            return tr_semantics;
        else
            return ts_semantics;
    }

    public void setSemantics(double[] semantics, TargetType target) {
        if(target == TargetType.TRAINING)
            tr_semantics = semantics;
        else
            ts_semantics = semantics;
    }

    public void setRMSE(double rmse, TargetType target) {
        if(target == TargetType.TRAINING)
            tr_rmse = rmse;
        else
            ts_rmse = rmse;
    }

    public double getRMSE(TargetType target){
        if(target == TargetType.TRAINING)
            return tr_rmse;
        return ts_rmse;
    }

    @Override
    public double getComparableValue() {
        return getRMSE(TargetType.TRAINING);
    }
    
//    @Override
//    public void calculateFitness(Node tree) {
//        setValues(data.training, tree);
//        setValues(data.test, tree);
//    }
    
//    public void setSemantics(double[] new_semantics, Target target){
//        Dataset dataset = null;
//        if(target == Target.TRAINING){
//            dataset = data.training;
//            tr_semantics = new_semantics;
//        }
//        else{
//            dataset = data.test;
//            ts_semantics = new_semantics;
//        }
//        double ss_res = 0;
//        for(int i = 0; i < new_semantics.length; i++){
//            Instance inst = dataset.get(i);
//            double tmp = inst.output - new_semantics[i];
//            ss_res += tmp * tmp;
//        }
//        if(target == Target.TRAINING)
//            tr_rmse= Math.sqrt(ss_res/dataset.size());
//        else
//            ts_rmse= Math.sqrt(ss_res/dataset.size());
//    }
//    
//    private void setValues(Dataset dataset, Node tree){
//        
//        double ss_res = 0;
//        for(int i = 0; i < dataset.size(); i++){
//            Instance inst = dataset.get(i);
//            metric.semantics[i] = tree.eval(inst.input);
//            
//            double tmp = inst.output-metric.yMean;
//            tmp = inst.output - metric.semantics[i];
//            ss_res += tmp * tmp;
//        }
//        metric.rmse = Math.sqrt(ss_res/dataset.size());
//    }
    
//    private class Metric{
//        double[] semantics;
//        double rmse;
//
//        public Metric(Dataset dataset) {
//            semantics = new double[dataset.size()];
//            rmse = 0;
//        }
//    }
}
