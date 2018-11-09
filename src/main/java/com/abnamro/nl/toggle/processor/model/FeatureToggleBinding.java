package com.abnamro.nl.toggle.processor.model;

import javax.lang.model.type.TypeMirror;

public class FeatureToggleBinding extends ToggleBinding {

    private final String toggleName;
    private final String expirationData;

    public FeatureToggleBinding(TypeMirror type, String elmName, String toggleName, String expirationData) {
       super(type, elmName);
        this.toggleName = toggleName;
        this.expirationData = expirationData;
    }

    public String getToggleName() {
        return toggleName;
    }

    public String getExpirationData() {
        return expirationData;
    }
}