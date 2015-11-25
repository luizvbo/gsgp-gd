/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 *
 * @author luiz
 */
public class Population {
    protected ArrayList<Individual> individuals;
    protected boolean initialized;
    
    public Population() {
        individuals = new ArrayList<>();
        initialized = false;
    }
    
    public Population(ArrayList<Individual> individuals) {
        this.individuals = new ArrayList<>();
        initialized = true;
    }
    
    public Individual getIndividual(int index){
        return individuals.get(index);
    }

    public ArrayList<Individual> getIndividuals() {
        return individuals;
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
    
    public void addAll(Individual[] newIndividuals){
        individuals.addAll(Arrays.asList(newIndividuals));
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

    public void add(Individual individual) {
        individuals.add(individual);
    }
}
