# ToggleProc
Annotate your toggles with this library

# Getting started
Add the dependency
```
dependencies {
  compile 'com.jakewharton:butterknife:8.8.1'
  annotationProcessor 'com.jakewharton:butterknife-compiler:8.8.1'
  
  compile files('com.abnarmo.nl:toggleproc:1.1')
  annotationProcessor files('com.abnarmo.nl:toggleproc:1.1')
}
```

# Configuration class

The configuration file is annotated with `@FeatureToggleConfiguration`, which expects 2 parameters:
- strictnessPolicy {COOL, MODERATE, HARSH}
  - COOL: no build error, only warnings
  - MODERATE: build error when the toggle is expired, in other cases build warning
  - HARSH: build error when toggle is expired, the number of toggles exceed the maxNumberOfToggles and when the toggles are too futuristic (a period of 8 weeks)
 - maxNumberOfToggles: max number of toggles allowed in this configuration file. 
```
@FeatureToggleConfiguration(strictnessPolicy = FeatureToggleConfiguration.StrictnessPolicy.COOL, maxNumberOfToggles = 6)
public class Config{
}
```
Define the toggles in the configuration file, the toggles are fields of type boolean with accessibility modifier default (if the manager class and configuration class are in the samen package) or public. Toggles are annotated by `@FeatureToggleBind(expirationDate = "yyyy-MM-dd", toggleName = "name")`, which has to params
- expirationDate: yyyy-MM-dd, e.g. 2017-12-30
- name: a name

```
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
}
```

# Manager class

The manager class is responsible for the implementation of the toggle. It implements the FeatureToggler interface:
```
public interface FeatureToggler {
    boolean isEnabled(String toggleName);
}
```
The method isEnabled accepts accepts toggleName as parameter and returns true if the toggle is enabled.



