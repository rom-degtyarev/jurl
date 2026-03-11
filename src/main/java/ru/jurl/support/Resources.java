package ru.jurl.support;

import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import static java.util.stream.Collectors.joining;
import static ru.jurl.support.Messages.DEFAULT_CHARSET;
import static ru.jurl.support.Strings.isEmpty;

public class Resources {
    public static final String PATH_PREFIX = "path:";
    public static final String FILE_PREFIX = "file:";
    public static final String CLASSPATH_PREFIX = "classpath:";

    public static boolean isResource(String string) {
        return !isEmpty(string) &&
                (string.startsWith(PATH_PREFIX) ||
                        string.startsWith(FILE_PREFIX) ||
                        string.startsWith(CLASSPATH_PREFIX)
                );
    }

    @SneakyThrows
    public static Supplier<String> of(String location) {
        Require.notNull(location, () -> "Location string is null");
        if (location.startsWith(PATH_PREFIX)) {
            return path(location);
        }
        if (location.startsWith(FILE_PREFIX)) {
            return file(location);
        }
        if (location.startsWith(CLASSPATH_PREFIX)) {
            return resource(location);
        }
        throw new IllegalArgumentException("Resource location not supported: " + location);
    }

    @SneakyThrows
    public static Supplier<String> resource(String location) {
        Require.notNull(location, () -> "Resource location is null");
        location = location.trim();
        if (location.startsWith(CLASSPATH_PREFIX)) {
            location = location.substring(CLASSPATH_PREFIX.length());
        }
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = ccl.getResourceAsStream(location.trim());
        return () -> read(inputStream, DEFAULT_CHARSET);
    }

    public static Supplier<String> file(String location) {
        Require.notNull(location, () -> "File location is null");
        location = location.trim();
        if (location.startsWith(FILE_PREFIX)) {
            location = location.substring(FILE_PREFIX.length());
        }
        File file = new File(location.trim());
        return () -> read(file);
    }

    public static Supplier<String> path(String location) {
        Require.notNull(location, () -> "Path location is null");
        location = location.trim();
        if (location.startsWith(PATH_PREFIX)) {
            location = location.substring(PATH_PREFIX.length());
        }
        Path path = Paths.get(location.trim());
        return () -> read(path);
    }

    @SneakyThrows
    public static String read(InputStream input, Charset charset) {
        try (
                InputStream stream = input;
                Reader reader = new InputStreamReader(stream, charset);
                BufferedReader buffer = new BufferedReader(reader)
        ) {
            return buffer.lines().collect(joining("\n"));
        }
    }

    @SneakyThrows
    public static String read(File file) {
        return Files.readString(file.toPath());
    }

    @SneakyThrows
    public static String read(Path path) {
        return Files.readString(path);
    }
}
