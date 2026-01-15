package ru.jurl.support;

import lombok.SneakyThrows;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.stream.Collectors.joining;
import static ru.jurl.support.Messages.DEFAULT_CHARSET;

public class Resources {

    public static final String PATH_PREFIX = "path:";
    public static final String FILE_PREFIX = "file:";
    public static final String CLASSPATH_PREFIX = "classpath:";

    @SneakyThrows
    public static String readString(String location) {
        Require.notNull(location, () -> "Location string is null");
        if (location.startsWith(PATH_PREFIX)) {
            String pathLocation = location.substring(PATH_PREFIX.length()).trim();
            Path path = Paths.get(pathLocation);
            return Files.readString(path);
        }
        if (location.startsWith(FILE_PREFIX)) {
            String fileLocation = location.substring(FILE_PREFIX.length()).trim();
            File file = new File(fileLocation);
            return Files.readString(file.toPath());
        }
        if (location.startsWith(CLASSPATH_PREFIX)) {
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            String resourceLocation = location.substring(CLASSPATH_PREFIX.length()).trim();
            try (
                    InputStream inputStream = ccl.getResourceAsStream(resourceLocation);
                    Reader reader = new InputStreamReader(inputStream, DEFAULT_CHARSET);
                    BufferedReader buffer = new BufferedReader(reader)
            ) {
                return buffer.lines().collect(joining("\n"));
            }
        }
        throw new IllegalArgumentException("Resource location not supported: " + location);
    }
}
