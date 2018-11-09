package com.abnamro.nl.toggle.processor.model;;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class BindingSet {
    private final Map<PackageElement, PackageBinding> packageBindingMap;

    public BindingSet() {
        this.packageBindingMap = new HashMap<>();
    }

    public Collection<PackageBinding> getPackageBindings() {
        return packageBindingMap.values();
    }

    public PackageBinding getPackageBinding(PackageElement packageElement) {
        PackageBinding binding = packageBindingMap.get(packageElement);
        if(binding == null){
            binding = new PackageBinding(packageElement);
            packageBindingMap.put(packageElement, binding);
        }
        return binding;
    }

    public void addBinding(PackageElement packageElement, TypeElement classElement,
                           ToggleBinding elementBinding) {
        PackageBinding packageBinding = getPackageBinding(packageElement);
        ClassBinding classBinding = packageBinding.getClassBinding(classElement);
        classBinding.addToggleBinding(elementBinding);
    }

}