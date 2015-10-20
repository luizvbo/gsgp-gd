/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sgp.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import sgp.MersenneTwister;
import sgp.population.Population;
import sgp.nodes.Node;
import sgp.nodes.functions.Function;
import sgp.nodes.terminals.Input;
import sgp.nodes.terminals.Terminal;
import sgp.population.builders.FullBuilder;
import sgp.population.builders.GrowBuilder;
import sgp.population.builders.HalfBuilder;
import sgp.population.Individual;
import sgp.population.builders.IndividualBuilder;
import sgp.population.selector.IndividualSelector;
import sgp.population.selector.TournamentSelector;

/**
 *
 * @author luiz
 */
public class PropertiesManager {
    protected boolean parameterLoaded;
    
    protected Properties fileParameters;
    protected CommandLine cliParameters;

    protected Options cliOptions;
    
    private DataProducer dataProducer;
    private MersenneTwister mersennePRNG;
    private ExperimentDataset data;
    private int numExperiments;
    private int numGenerations;
    private int numThreads;
    private int populationSize;
    private double minError;
    private double ms;
    private double mutProb;
    private double xoverProb;
    private double semSimThres;
    private Node[] terminals;
    private Function[] functions;
    
    private IndividualSelector individualSelector;
    
    private IndividualBuilder individualBuilder;
    private IndividualBuilder randomTreeBuilder;
    
    private String outputDir;
    private String filePrefix;
    

    private void loadParameters() throws Exception{
        dataProducer = getDataProducer();
        mersennePRNG = new MersenneTwister(getLongPropertie(ParameterList.SEED, System.currentTimeMillis()));
        terminals = getTerminals();
        functions = getFunctions();
        numExperiments = getIntegerPropertie(ParameterList.NUM_REPETITIONS, 1);
        numGenerations = getIntegerPropertie(ParameterList.NUM_GENERATION, 200);
        
        numThreads = getIntegerPropertie(ParameterList.NUMBER_THREADS, -1);
        if(numThreads == -1) numThreads = Runtime.getRuntime().availableProcessors();
        
        populationSize = getIntegerPropertie(ParameterList.POP_SIZE, 1000);
        minError = getDoublePropertie(ParameterList.MIN_ERROR, 0);
        ms = getDoublePropertie(ParameterList.MUT_STEP, -1);
        mutProb = getDoublePropertie(ParameterList.MUT_PROB, 0.5);
        xoverProb = getDoublePropertie(ParameterList.XOVER_PROB, 0.5);
        semSimThres = getDoublePropertie(ParameterList.SEMANTIC_SIMILARITY_THRESHOLD, 0.1);
        outputDir = getStringPropertie(ParameterList.PATH_OUTPUT_DIR, true);
        filePrefix = getStringPropertie(ParameterList.FILE_PREFIX, false);
        
        individualBuilder = getIndividualBuilder(false);
        randomTreeBuilder = getIndividualBuilder(true);
        
        individualSelector = getIndividualSelector();        
    }
    
    public enum ParameterList {

        PATH_DATA_FILE("experiment.data", "Path for the training/test files. See experiment.sampling option for more details", true),
        PATH_OUTPUT_DIR("experiment.output.dir", "Output directory", false),
        SEED("experiment.seed", "Seed (long int) used by the pseudo-random number generator", false),
        FILE_PREFIX("experiment.file.prefix", "Identifier prefix for files", false),
        TERMINAL_LIST("tree.build.terminals", "List of terminals used to build new trees (separeted by commas)", true),
        FUNCTION_LIST("tree.build.functions", "List of functions used to build new trees (separeted by commas)", true),
        INDIVIDUAL_BUILDER_POP("tree.build.builder", "Builder used to generate trees for the initial population", true),
        INDIVIDUAL_BUILDER_RAND_TREE("tree.build.builder.random.tree", "Builder used to generate random trees for the semantic operators", true),
        INDIVIDUAL_SELECTOR("pop.ind.selector", "Type of selector used to select individuals for next generations", true),
        
        EXPERIMENT_DESIGN("experiment.design", "Type of experiment (cross-validation or holdout):"
                                                + "\n# - If crossvalidation, uses splited data from a list of files. Use paths to the"
                                                + "\n# files in the form /pathToFile/repeatedName#repeatedName, where # indicates "
                                                + "\n# where the fold index is placed (a number from 0 to k-1). E.g. /home/iris-#.dat,"
                                                + "\n# with 3 folds in the path will look for iris-0.dat, iris-1.dat and iris-2.dat"
                                                + "\n# - If holdout, Use paths to the files in the form /pathToFile/repeatedName#repeatedName,"
                                                + "\n# where # is composed by the pattern (train|test)-i with i=0,1,...,n-1, where n is"
                                                + "\n# the number of experiment files. E.g. /home/iris-#.dat, with 4 files (2x(train+test))"
                                                + "\n# in the path will look for iris-train-0.dat, iris-test-0.dat, iris-train-1.dat and iris-test-1.dat", true),
        
        MAX_TREE_DEPTH("tree.build.max.depth", "Max depth allowed when building trees", false),
        MIN_TREE_DEPTH("tree.min.depth", "Min depth allowed when building trees", false),
        NUM_GENERATION("evol.num.generation", "Number of generations", false),
        NUM_REPETITIONS("experiment.num.repetition", "Number of experiment repetitions (per fold) - default = 1", false),
        POP_SIZE("pop.size", "Population size", false),
        NUMBER_THREADS("evol.num.threads", "Number of threads (for parallel execution)", false),
        TOURNAMENT_SIZE("pop.ind.selector.tourn.size", "Tournament size, when using tournament as selector", false),
        
        MIN_ERROR("evol.min.error", "Minimum error to consider a hit", false),
        MUT_PROB("breed.mut.prob", "Probability of applying the mutation operator", false),
        MUT_STEP("breed.mut.step", "Mutation step", false),
        XOVER_PROB("breed.xover.prob", "Probability of applying the crossover operator", false),
        SEMANTIC_SIMILARITY_THRESHOLD("sem.gp.epsilon", "Threshold used to determine if two semantics are similar", false);

        public final String name;
        public final String description;
        public final boolean mandatory;

        private ParameterList(String name, String description, boolean mandatory) {
            this.name = name;
            this.description = description;
            this.mandatory = mandatory;
        }
    }

    public PropertiesManager(String args[]) throws Exception{
        setOptions();
        parameterLoaded = loadParameterFile(args);
        loadParameters();        
    }

    /**
     * Load the parameters from the CLI and file
     * @param args CLI parameters
     * @return True if and only if parameters are loaded both from CLI and file
     * @throws Exception 
     */
    private boolean loadParameterFile(String[] args) throws Exception{
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine parametersCLI = parser.parse(cliOptions, args);
            if(parametersCLI.hasOption("H")){
                writeParameterModel();
                return false;
            }
            if(!parametersCLI.hasOption("p"))
                throw new Exception("The parameter file was not specied.");
            String path = parametersCLI.getOptionValue("p");
            path = path.replaceFirst("^~",System.getProperty("user.home"));
            File parameterFile = new File(path);
            if(!parameterFile.canRead()) 
                throw new Exception("Parameter file can not be read.");
            FileInputStream fileInput = new FileInputStream(parameterFile);
            fileParameters = new Properties();
            fileParameters.load(fileInput);
            return true;
        } 
        catch (MissingOptionException ex){
            throw new Exception("Required parameter not found.");
        }
        catch (ParseException ex) {
            throw new Exception("Error while parsing the command line.");
        }
    }
    private void writeParameterModel(){
        try{
            File file = new File("model.param");

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            StringBuilder textToPrint = new StringBuilder();
            for(ParameterList p : ParameterList.values()){
                textToPrint.append("# " + p.description + "\n");
                textToPrint.append(p.name + " = \n");
            }
            bw.write(textToPrint.toString());
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private IndividualSelector getIndividualSelector() throws Exception{
        String value = getStringPropertie(ParameterList.INDIVIDUAL_SELECTOR, false).toLowerCase();
        IndividualSelector indSelector;
        switch(value){
            case "tournament":
                indSelector = new TournamentSelector(getIntegerPropertie(ParameterList.TOURNAMENT_SIZE, 7));
                break;
            default:
                throw new Exception("The inidividual selector must be defined.");
        }
        return indSelector;
    }
    
    public DataProducer getDataProducer() throws Exception{
        String value = getStringPropertie(ParameterList.EXPERIMENT_DESIGN, false).toLowerCase();
        DataProducer dataProducer;
        switch(value){
            case "crossvalidation":
                dataProducer = new CrossvalidationHandler();
                break;
            case "holdout":
                dataProducer = new HoldoutHandler();
                break;
            default:
                throw new Exception("Experiment design must be crossvalidation or holdout.");
        }
        dataProducer.setDataset(getStringPropertie(ParameterList.PATH_DATA_FILE, true));
        return dataProducer;
    }
    
    private int getIntegerPropertie(ParameterList key, int defaultValue) throws Exception {
        try {
            if (!fileParameters.containsKey(key.name) && key.mandatory) {
                throw new NoSuchFieldException("The input parameter (" + key.name + ") was not found");
            }
            else if(!fileParameters.containsKey(key.name) || fileParameters.getProperty(key.name).replaceAll("\\s", "").equals(""))
                return defaultValue;
            return Integer.parseInt(fileParameters.getProperty(key.name));
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage() + "\nThe input parameter (" + key.name + ") could not be converted to int.");
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage() + "\nThe parameter file was not initialized.");
        }
    }
    
    private long getLongPropertie(ParameterList key, long defaultValue) throws Exception {
        try {
            if (!fileParameters.containsKey(key.name) && key.mandatory) {
                throw new NoSuchFieldException("The input parameter (" + key.name + ") was not found");
            }
            else if(!fileParameters.containsKey(key.name) || fileParameters.getProperty(key.name).replaceAll("\\s", "").equals(""))
                return defaultValue;
            return Integer.parseInt(fileParameters.getProperty(key.name));
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage() + "\nThe input parameter (" + key.name + ") could not be converted to int.");
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage() + "\nThe parameter file was not initialized.");
        }
    }

    private Double getDoublePropertie(ParameterList key, double defaultValue) throws Exception {
        try {
            if (!fileParameters.containsKey(key.name) && key.mandatory) {
                throw new NoSuchFieldException("The input parameter (" + key.name + ") was not found");
            }
            else if(!fileParameters.containsKey(key.name))
                return defaultValue;
            return Double.parseDouble(fileParameters.getProperty(key.name));
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage() + "\nThe input parameter (" + key.name + ") could not be converted to double.");
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage() + "\nThe parameter file was not initialized.");
        }
    }
    
    private String getStringPropertie(ParameterList key, boolean isFile) throws Exception {
        try {
            if (!fileParameters.containsKey(key.name) && key.mandatory) {
                throw new NoSuchFieldException("The input parameter (" + key.name + ") was not found");
            }
            else if(!fileParameters.containsKey(key.name))
                return null;
            String output = fileParameters.getProperty(key.name);
            if(isFile)
                output = output.replaceFirst("^~",System.getProperty("user.home"));
            return output;
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage() + "\nThe parameter file was not initialized.");
        }
    }

    private IndividualBuilder getIndividualBuilder(boolean isForRandomTrees) throws Exception{
        String builderType;
        if(isForRandomTrees)
            builderType = getStringPropertie(ParameterList.INDIVIDUAL_BUILDER_RAND_TREE, false).toLowerCase();
        else
            builderType = getStringPropertie(ParameterList.INDIVIDUAL_BUILDER_POP, false).toLowerCase();
        int maxDepth = getIntegerPropertie(ParameterList.MAX_TREE_DEPTH, 6);
        int minDepth = getIntegerPropertie(ParameterList.MIN_TREE_DEPTH, 2);
        switch(builderType){
            case "grow":
                return new GrowBuilder(maxDepth, minDepth, functions, terminals, mersennePRNG);
            case "full":
                return new FullBuilder(maxDepth, minDepth, functions, terminals, mersennePRNG);
            case "rhh":
                return new HalfBuilder(maxDepth, minDepth, functions, terminals, mersennePRNG);
            default:
                throw new Exception("There is no builder called " + builderType + ".");
        }
    }
    
    public boolean isParameterLoaded() {
        return parameterLoaded;
    }
    
    private Node[] getTerminals() throws Exception{
        String[] sTerminals = getStringPropertie(ParameterList.TERMINAL_LIST, false).replaceAll("\\s", "").split(",");
        boolean useAllInputs = true;
        for(String str : sTerminals){
            if(str.toLowerCase().matches("x\\d+")){
                useAllInputs = false;
                break;
            }
        }
        ArrayList<Node> terminals = new ArrayList<Node>();
        if(useAllInputs){
            for(int i = 0; i < dataProducer.getNumInputs(); i++) terminals.add(new Input(i));
            for(String str : sTerminals){
                Class<?> terminal = Class.forName(str);
                Terminal newTerminal = (Terminal)terminal.newInstance();
                newTerminal.setup(mersennePRNG);
                terminals.add(newTerminal);
            }
        }
        else{
            // ************************ TO IMPLEMENT ************************
        }
        return terminals.toArray(new Node[terminals.size()]);
    }

    /**
     * Get the list of functions from the String read from the file
     * @return An array of functions
     * @throws Exception Parameter not found, error while parsing the function type 
     */
    private Function[] getFunctions()throws Exception{
        String[] sFunctions = getStringPropertie(ParameterList.FUNCTION_LIST, false).replaceAll("\\s", "").split(",");
        ArrayList<Function> functionArray = new ArrayList<Function>();
        for(String str : sFunctions){
            Class<?> function = Class.forName(str);
            functionArray.add((Function)function.newInstance());
        }
        return functionArray.toArray(new Function[functionArray.size()]);
    }

    /**
     * There are tree input options from the CLI: 
     * - The path for the parameter file
     * - One or more parameters to overwrite parameters from the file 
     * - The option of creating a parameter file model on the classpath
     */
    private void setOptions() {
        cliOptions = new Options();
        cliOptions.addOption(Option.builder("p")
                .required(false)
                .hasArg()
                .desc("Paramaters file")
                .type(String.class)
                .build());
        cliOptions.addOption(Option.builder("P")
                .required(false)
                .hasArg()
                .desc("Overwrite of one or more parameters provided by file.")
                .type(String.class)
                .hasArgs()
                .build());
        cliOptions.addOption(Option.builder("H")
                .required(false)
                .desc("Create a parameter file model on the classpath.")
                .type(String.class)
                .build());
    }

    public MersenneTwister getMersennePRNG() {
        return mersennePRNG;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public int getNumExperiments() {
        return numExperiments;
    }

    public int getNumGenerations() {
        return numGenerations;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public double getMinError() {
        return minError;
    }

    public double getMutationStep() {
        return ms;
    }

    public double getMutProb() {
        return mutProb;
    }

    public double getXoverProb() {
        return xoverProb;
    }

    public double getSemSimThreshold() {
        return semSimThres;
    }
    
    public String getOutputDir() {
        return outputDir;
    }

    public String getFilePrefix() {
        return filePrefix;
    }
    
    public Node getRandomTree(){
        return randomTreeBuilder.newRootedTree(0);
    }
    
    public Node getNewIndividualTree(){
        return individualBuilder.newRootedTree(0);
    }
    
    public Individual selectIndividual(Population population){
        return individualSelector.selectIndividual(population, mersennePRNG);
    }
}
