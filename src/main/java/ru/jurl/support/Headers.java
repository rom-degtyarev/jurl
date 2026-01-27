package ru.jurl.support;

import ru.jurl.http.Header;
import ru.jurl.http.headers.ContentDisposition;
import ru.jurl.http.headers.ContentType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;

public class Headers {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    public static final Predicate<Header> CONTENT_TYPE_FILTER = header -> header instanceof ContentType;

    public static <T extends Header> List<T> get(List<Header> headers, Predicate<Header> predicate) {
        if (headers == null) return emptyList();
        return (List<T>) headers.stream().filter(predicate).toList();
    }

    public static <T extends Header> Optional<T> getFirst(List<Header> headers, Predicate<Header> predicate) {
        List<T> list = get(headers, predicate);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public static Header valueOf(String headerName, String headerValue) {
        if (headerName.equalsIgnoreCase(CONTENT_TYPE))
            return new ContentType(headerValue);
        if (headerName.equalsIgnoreCase(CONTENT_DISPOSITION))
            return new ContentDisposition(headerValue);
        return new Header(headerName, headerValue);
    }

    public static Header valueOf(String headerLine) {
        String[] strings = headerLine.split(":");
        String headerName = strings[0].trim();
        String headerValue = strings[1].trim();
        return valueOf(headerName, headerValue);
    }
}
