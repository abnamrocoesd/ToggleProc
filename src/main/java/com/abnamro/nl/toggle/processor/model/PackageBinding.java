package com.abnamro.nl.toggle.processor.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public class PackageBinding {
    private PackageElement packageElement;
    private Map<TypeElement, ClassBinding> classBindings;
    private final Map<String, Object> properties = new HashMap<>();

    public PackageBinding(PackageElement packageElement) {
        this.packageElement = packageElement;
        this.classBindings = new HashMap<>();
    }

    public String getPackageName() {
        return packageElement.getQualifiedName().toString();
    }

    public Collection<ClassBinding> getClassBindings() {
        return classBindings.values();
    }

    public ClassBinding getClassBinding(TypeElement typeElement) {
        ClassBinding binding = classBindings.get(typeElement);
        if(binding == null){
            binding = new ClassBinding(typeElement);
            classBindings.put(typeElement, binding);
        }
        return binding;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}