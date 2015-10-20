/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sgp;




import sgp.data.ExperimentDataset;
import sgp.data.DataProducer;
import sgp.data.DataWriter;
import sgp.data.PropertiesManager;

/**
 *
 * @author luiz
 */
public class Experimenter {    
    protected PropertiesManager parameters;
    
    protected DataProducer dataProducer;
    
    public Experimenter(String[] args) throws Exception{
        setup(args);
        if(parameters.isParameterLoaded())
            execute();
    }
    
    private void execute(){
        try {
            Statistics[] stats = new Statistics[parameters.getNumExperiments()];
            
            
            DataWriter.resetInitialSemantics(parameters.getOutputDir(), parameters.getFilePrefix());
            
            
            // Run the algorithm for a defined number of repetitions
            for(int execution = 0; execution < parameters.getNumExperiments(); execution++){
                System.out.println("Execution " + (execution+1) + ":");
                ExperimentDataset data = parameters.getDataProducer().getExperimentDataset();
                SGP sgp = new SGP(data, parameters);
                sgp.evolve();
                stats[execution] = sgp.getStatistics();
            
                                
                DataWriter.writeInitialSemantics(parameters.getOutputDir(), parameters.getFilePrefix(), stats[execution]);
                stats[execution].resetInitialSemantics();
                
            }
            DataWriter.writeResults(parameters.getOutputDir(), parameters.getFilePrefix(), stats);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Reads inputs, initialize the TrainingData object and load the parameter file
     * @param args Input parameters from command line
     */
    protected final void setup(String[] args){
        try{    
            parameters = new PropertiesManager(args);
        }
        catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }
}
