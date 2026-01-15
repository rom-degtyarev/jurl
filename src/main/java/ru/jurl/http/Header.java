package ru.jurl.http;

import lombok.Data;

@Data
public class Header {
    private final String name;
    private final String value;

    @Override
    public String toString() {
        return name + ": " + value;
    }
}
