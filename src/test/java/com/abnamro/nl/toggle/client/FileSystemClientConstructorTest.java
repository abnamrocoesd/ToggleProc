package com.abnamro.nl.toggle.client;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileSystemClientConstructorTest {

    @Test
    public void should_use_default_file_name_if_it_not_passed_through_constructor() {
        FileSystemClient client = new FileSystemClient(null);
        assertEquals("features.yml", client.getFileName());
    }

    @Test
    public void should_use_default_file_name_if_it_is_null() {
        FileSystemClient client = new FileSystemClient(null, null);
        assertEquals("features.yml", client.getFileName());
    }

    @Test
    public void should_use_name_passed_through_constructor() {
        FileSystemClient client = new FileSystemClient("filename.yml", null);
        assertEquals("filename.yml", client.getFileName());
    }


}
