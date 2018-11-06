package com.abnamro.nl.toggle.client;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class FileSystemClient {

    private final String DEFAULT_FILE_NAME = "features.yml";

    private String fileName = DEFAULT_FILE_NAME;
    private final Map<String, Boolean> defaultValues;

    public FileSystemClient(String featuresFileName, Map<String, Boolean> defaultValues) {
        this.defaultValues = defaultValues;
        if (featuresFileName != null && !featuresFileName.isEmpty()) {
            this.fileName = featuresFileName;
        }
    }

    public FileSystemClient(Map<String, Boolean> defaultValues) {
        this.defaultValues = defaultValues;
    }

    public boolean isEnabled(String name) {
        Map<String, Boolean> features = getFeaturesFromFile();
        return getFromFileOrDefaultValue(name, features);
    }

    protected String getFileName() {
        return fileName;
    }

    private boolean getFromFileOrDefaultValue(String name, Map<String, Boolean> features) {
        Boolean value = features.get(name);
        if (value != null) {
            return value;
        }
        return defaultValues.get(name);
    }

    private Map<String, Boolean> getFeaturesFromFile() {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
        if (is != null) {
            return new Yaml().load(is);
        }
        return defaultValues;
    }


}
