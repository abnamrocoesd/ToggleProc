package cielo.toggleproc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by bsobat on 10/11/2017.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface FeatureToggleConfiguration {
    enum StrictnessPolicy {COOL, MODERATE, HARSH}
    int maxNumberOfToggles();
    StrictnessPolicy strictnessPolicy();

}
