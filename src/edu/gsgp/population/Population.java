/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population;

import java.util.ArrayList;
import java.util.Collections;
import edu.gsgp.data.PropertiesManager;


/**
 *
 * @author luiz
 */
public class Population {
    protected ArrayList<Individual> individuals;
    protected PropertiesManager properties;
    protected boolean initialized;
    
    public Population(PropertiesManager properties) {
        this.properties = properties;
        individuals = new ArrayList<>();
        initialized = false;
    }
    
    public Individual getIndividual(int index){
        return individuals.get(index);
    }

    public ArrayList<Individual> getIndividuals() {
        return individuals;
    }

    public PropertiesManager getProperties() {
        return properties;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
    
    public void setCurrentPopulation(ArrayList<Individual> currentPopulation) {
        this.individuals = currentPopulation;
    }
    
    public void addAll(ArrayList<Individual> newIndividuals){
        individuals.addAll(newIndividuals);
    }
    
    public int size(){
        return individuals.size();
    }
    
    public Individual getBestIndividual(){
        Collections.sort(individuals);
        return individuals.get(0);
    }

    public boolean isInitialized() {
        return initialized;
    }
}
