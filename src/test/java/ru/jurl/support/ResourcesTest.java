package ru.jurl.support;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ResourcesTest {
    @Test
    public void read_resource_content() {
        assertNotNull(Resources.readString("file:src/test/resources/test.json"));
        assertNotNull(Resources.readString("path:src/test/resources/test.json"));
        assertNotNull(Resources.readString("classpath:test.json"));
    }
}