/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.builder.individual;

import edu.gsgp.data.PropertiesManager;

/**
 *
 * @author luiz
 */
public abstract class IndividualBuilder {
    protected PropertiesManager properties;

    public IndividualBuilder(PropertiesManager properties) {
        this.properties = properties;
    }
}
