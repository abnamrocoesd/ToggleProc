package com.abnamro.nl.toggle.annotation;

/**
 * Created by bsobat on 08/11/2017.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FeatureToggleBind {
    //yyyy-MM-dd
    String toggleName();
    String expirationDate();
}
