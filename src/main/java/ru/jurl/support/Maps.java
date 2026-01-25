package ru.jurl.support;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class Maps {
    public static <T> Map<T, T> mapOf(T... args) {
        if (args == null) return Map.of();
        Require.isTrue(args.length % 2 == 0, () -> "Map entries array even size expected");
        Map<T, T> map = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            map.put(args[i], args[i + 1]);
        }
        return map;
    }

    public static <K, V, T> Map<K, V> map(Map<T, T> map, Function<Map.Entry<T, T>, Map.Entry<K, V>> mapper) {
        return map.entrySet()
                .stream()
                .map(mapper)
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (value1,value2) -> value2
                ));
    }
}
