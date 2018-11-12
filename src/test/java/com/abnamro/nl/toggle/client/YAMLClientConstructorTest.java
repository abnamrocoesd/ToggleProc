package com.abnamro.nl.toggle.client;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class YAMLClientConstructorTest {

    @Test
    public void should_use_default_file_name_if_its_empty() {
        YAMLClient client = new YAMLClient("", null);
        assertEquals("src/main/resources/features.yml", client.getFileName());
    }

    @Test
    public void should_use_default_file_name_if_it_is_null() {
        YAMLClient client = new YAMLClient(null, null);
        assertEquals("src/main/resources/features.yml", client.getFileName());
    }

    @Test
    public void should_use_name_passed_through_constructor() {
        YAMLClient client = new YAMLClient("filename.yml", null);
        assertEquals("filename.yml", client.getFileName());
    }


}
