package ru.jurl.http;

import java.util.function.Supplier;

public record Parameter(
        String name,
        Supplier<String> value
) implements Supplier<String> {
    @Override
    public String get() {
        return value.get();
    }

    public static Parameter parameter(String name, String value) {
        return new Parameter(name, () -> value);
    }

    public static Parameter parameter(String name, Supplier<String> value) {
        return new Parameter(name, value);
    }
}
