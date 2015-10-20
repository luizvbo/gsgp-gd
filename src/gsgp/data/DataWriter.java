/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gsgp.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import gsgp.Statistics;

/**
 *
 * @author luiz
 */
public class DataWriter {
    public static void writeResults(String outputPath,
                                    String outputPrefix, 
                                    Statistics[] statsArray) throws Exception{
        File outputDir = getOutputDir(outputPath);
        outputDir = new File(outputDir.getAbsolutePath()+ File.separator + outputPrefix);
        outputDir.mkdirs();
        // Object to write results on file
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "trFitness.csv"));
        bw.write(getStatisticsFromArray(statsArray, Statistics.StatsType.BEST_OF_GEN_TR_FIT));
        bw.close();
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "tsFitness.csv"));
        bw.write(getStatisticsFromArray(statsArray, Statistics.StatsType.BEST_OF_GEN_TS_FIT));
        bw.close();
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "individualSize.csv"));
        bw.write(getStatisticsFromArray(statsArray, Statistics.StatsType.BEST_OF_GEN_SIZE));
        bw.close();
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "initialSemantics.csv"));
//        bw.write(getStatisticsFromArray(statsArray, Statistics.StatsType.INITIAL_SEMANTICS));
//        bw.close();
    }
    
    public static void writeInitialSemantics(String outputPath,
                                    String outputPrefix, 
                                    Statistics stats) throws Exception{
        File outputDir = getOutputDir(outputPath);
        outputDir = new File(outputDir.getAbsolutePath()+ File.separator + outputPrefix);
        outputDir.mkdirs();
        // Object to write results on file
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "initialSemantics.csv", true));
        bw.write(stats.asWritableString(Statistics.StatsType.INITIAL_SEMANTICS) + "\n");
        bw.close();
    }
    
    public static void resetInitialSemantics(String outputPath,
                                    String outputPrefix) throws Exception{
        File outputDir = getOutputDir(outputPath);
        outputDir = new File(outputDir.getAbsolutePath()+ File.separator + outputPrefix);
        outputDir.mkdirs();
        // Object to write results on file
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "initialSemantics.csv"));
        bw.write("");
        bw.close();
    }
    
    
    /**
     * Selects the path to save output data.
     * @param outputPath Path to a directory to write the output data
     * @return File object pointing to the output directory
     */
    protected static File getOutputDir(String outputPath){
        File outputDir;
        if(!outputPath.equals("")){
            outputDir = new File(outputPath);
        }
        else{
            outputDir = new File(System.getProperty("user.dir"));
        }  
        return outputDir;
    }

    protected static String getStatisticsFromArray(Statistics[] statsArray, Statistics.StatsType type) {
        StringBuilder str = new StringBuilder();
        for(Statistics stats : statsArray){
            str.append(stats.asWritableString(type) + "\n");
        }
        return str.toString();
    }
}
