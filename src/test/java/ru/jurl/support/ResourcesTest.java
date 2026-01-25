package ru.jurl.support;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ResourcesTest {
    @Test
    public void read_resource_content() {
        assertNotNull(Resources.of("file:src/test/resources/test.json").get());
        assertNotNull(Resources.of("path:src/test/resources/test.json").get());
        assertNotNull(Resources.of("classpath:test.json").get());
    }
}