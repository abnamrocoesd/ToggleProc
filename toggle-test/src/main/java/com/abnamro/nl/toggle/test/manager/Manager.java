package com.abnamro.nl.toggle.test.manager;

import com.abnamro.nl.toggle.annotation.FeatureToggleConfiguration;
import com.abnamro.nl.toggle.toggler.FeatureToggler;

public class Manager implements FeatureToggler {
    private Config config;
    public Manager(){
        config = new Config();
        FeatureToggleBinder.bind(this, config);
    }
    public boolean isEnabled(String name) {
        return false;
    }

    public static void main(String[] test){
        Manager manager = new Manager();
        System.out.println("Test1 "+manager.config.test1);
    }
}
