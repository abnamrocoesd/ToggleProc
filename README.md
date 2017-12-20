
# ToggleProc
Make your toggles manageable with this library.

Features:
* Build-time information about the toggles such as:
  * toggle expiration
  * exceeding number of toggles
  * Too futuristic toggles
* Configure whether the build breaks or gives warning when the above conditions are not met
* Use annotation to define the toggles
* TODO: use this library for A/B testing

# Getting started
Add the dependency
```
repositories {
     mavenCentral()
}

dependencies {
  //...
  compile('com.abnamro.nl:toggleproc:1.1')
  annotationProcessor('com.abnamro.nl:toggleproc:1.1')
  //...
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
Define the toggles in the configuration file, the toggles are fields of type boolean with accessibility modifier default (if the manager class and configuration class are in the samen package) or public. Toggles are annotated by `@FeatureToggleBind(expirationDate = "yyyy-MM-dd", toggleName = "name")`, which has two parameters
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
```

# Manager class

The manager class is responsible for the implementation of the toggle. It implements the FeatureToggler interface:
```
public interface FeatureToggler {
    boolean isEnabled(String toggleName);
}
```
The method isEnabled accepts toggleName as parameter and returns true if the toggle is enabled. For example if you use Firebase Remote Config the its implementation will be something like
```
public class FeatureToggleManager implements FeatureToggler {
    private final FeatureToggleConfig config;


    protected FeatureToggleManager(Config config) {
        this.config = config;
        FeatureToggleBinder.bind(this, config);
    }

    public Config getToggles() {
        return config;
    }

    @override
    public boolean isEnabled(final String name){
        return mFirebaseRemoteConfig.getBoolean(name);
    }
}
```

The manager uses the generated `FeatureToggleBinder.bind(this, config)` to bind the values.

# Usage

To use, just simply create a new instance of Manager (or use singleton) and use the `getToggles()`.
```
FeatureToggleManager manager = new FeatureToggleManager(new Config);
boolean isTest1Enabled = manager.getToggles().isTest1();
...
```

