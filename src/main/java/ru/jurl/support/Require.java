package ru.jurl.support;

import java.util.function.Supplier;

public class Require {
    public static  <T> void notNull(T value, Supplier<String> errorMessage) {
        if (value == null) throw new IllegalArgumentException(errorMessage.get());
    }

    public static void isTrue(boolean value, Supplier<String> errorMessage) {
        if (!value) throw new IllegalArgumentException(errorMessage.get());
    }
}
