package com.abnamro.nl.toggle.test.manager;

import com.abnamro.nl.toggle.annotation.FeatureToggleBind;
import com.abnamro.nl.toggle.annotation.FeatureToggleConfiguration;

@FeatureToggleConfiguration(strictnessPolicy = FeatureToggleConfiguration.StrictnessPolicy.COOL, maxNumberOfToggles = 3)
public class Config {
    @FeatureToggleBind(expirationDate = "2018-01-01", toggleName = "test1")
    boolean test1;
    @FeatureToggleBind(expirationDate = "2018-01-01", toggleName = "test2")
    boolean test2;
    @FeatureToggleBind(expirationDate = "2018-01-01", toggleName = "test3")
    boolean test3;
    @FeatureToggleBind(expirationDate = "2017-01-01", toggleName = "test4")
    boolean test4;

    public boolean isTest1() {
        return test1;
    }

    public boolean isTest2() {
        return test2;
    }

    public boolean isTest3() {
        return test3;
    }

    public boolean isTest4() {
        return test4;
    }
}
