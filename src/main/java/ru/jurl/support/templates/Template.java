package ru.jurl.support.templates;

import ru.jurl.http.Parameter;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

@FunctionalInterface
public interface Template<T> {
    T merge(Map<String, Parameter> parameters);

    default T merge(Parameter... parameters) {
        return merge(stream(parameters)
                .collect(toMap(
                        Parameter::name,
                        p -> p,
                        (p1, p2) -> p2
                )));
    }

    default T merge(List<Parameter> parameters) {
        return merge(parameters
                .stream()
                .collect(toMap(
                        Parameter::name,
                        p -> p,
                        (p1, p2) -> p2
                )));
    }
}
