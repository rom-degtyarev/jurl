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
}
