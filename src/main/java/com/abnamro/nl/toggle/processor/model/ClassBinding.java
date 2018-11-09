package com.abnamro.nl.toggle.processor.model;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.TypeElement;

public class ClassBinding {
    private final TypeElement classElement;
    private final Set<ToggleBinding> toggleBindings;

    public ClassBinding(TypeElement classElement) {
        this.classElement = classElement;
        this.toggleBindings = new HashSet<>();
    }

    public TypeElement getClassElement() {
        return classElement;
    }

    public Set<ToggleBinding> getToggleBindings() {
        return toggleBindings;
    }

    public void addToggleBinding(ToggleBinding toggleBinding) {
        toggleBindings.add(toggleBinding);
    }
}