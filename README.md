# ToggleProc
Annotate your toggles with this library

# Getting started
Add the dependency

dependencies {
  compile 'com.jakewharton:butterknife:8.8.1'
  annotationProcessor 'com.jakewharton:butterknife-compiler:8.8.1'
  
  compile files('com.abnarmo.nl:toggleproc:1.1')
  annotationProcessor files('com.abnarmo.nl:toggleproc:1.1')
}

# Configuration file

The configuration file is annotated with @FeatureToggleConfiguration, which expects 2 parameters:
- strictnessPolicy {COOL, MODERATE, HARSH}
  - COOL: no build error, only warnings
  - MODERATE: build error when the toggle is expired, in other cases build warning
  - HARSH: build error when toggle is expired, the number of toggles exceed the maxNumberOfToggles and when the toggles are too futuristic (a period of 8 weeks)
 - maxNumberOfToggles: max number of toggles allowed in this configuration file. 

@FeatureToggleConfiguration(strictnessPolicy = FeatureToggleConfiguration.StrictnessPolicy.COOL, maxNumberOfToggles = 6)
public class Config{
}





