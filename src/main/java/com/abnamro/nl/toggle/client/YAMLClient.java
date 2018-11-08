package com.abnamro.nl.toggle.client;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class YAMLClient {

    private final Map<String, Boolean> defaultValues;
    private final String DEFAULT_FILE_NAME = "src/main/resources/features.yml";

    private String fileName = DEFAULT_FILE_NAME;

    public YAMLClient(String featuresFileName, Map<String, Boolean> defaultValues) {
        this.defaultValues = defaultValues;

        if (featuresFileName != null && !featuresFileName.isEmpty()) {
            this.fileName = featuresFileName;
        }
    }

    public YAMLClient(Map<String, Boolean> defaultValues) {
        this(DEFAULT_FILE_NAME, defaultValues);
    }

    public boolean isEnabled(String name) {
        Map<String, Boolean> features = getStatusMap();

        Boolean featureStatus = features.get(name);
        if (isPresentOnFile(featureStatus)) return featureStatus;

        featureStatus = defaultValues.get(name);
        return isPresentOnDefaultValues(featureStatus) ? featureStatus : false;
    }

    private boolean isPresentOnDefaultValues(Boolean featureStatus) {
        return featureStatus != null;
    }

    private boolean isPresentOnFile(Boolean value) {
        return value != null;
    }

    String getFileName() {
        return fileName;
    }

    private Map<String, Boolean> getStatusMap() {
        InputStream is;
        try {
            is = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            return defaultValues;
        }
        return new Yaml().load(is);
    }

}
