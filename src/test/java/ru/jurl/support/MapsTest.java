package ru.jurl.support;

import org.junit.Test;

import java.util.Map;
import java.util.function.Supplier;

import static java.util.Map.entry;
import static org.junit.Assert.*;
import static ru.jurl.support.Maps.map;
import static ru.jurl.support.Maps.mapOf;

public class MapsTest {

    @Test
    public void mapOf_test() {
        assertTrue(mapOf(null).isEmpty());
        assertTrue(mapOf().isEmpty());
        assertEquals(1, mapOf("test", "test").size());
        assertThrows(IllegalArgumentException.class, () -> {
            mapOf("test1", "test2", "test3");
        });
    }

    @Test
    public void map_test() {
        //given
        Map<String, String> someMap = mapOf("test", "test");
        //when
        Map<String, Supplier<String>> otherMap = map(
                someMap,
                entry -> entry(entry.getKey(), entry::getValue)
        );
        //then
        assertEquals(someMap.size(), otherMap.size());
        assertEquals(someMap.get("test"), otherMap.get("test").get());
    }
}