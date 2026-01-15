package ru.jurl.support;

import ru.jurl.http.Extractor;
import ru.jurl.http.ResponseMessage;

import java.util.function.Function;

public class Extractors {
    public static <T> Extractor<T> Last(Function<ResponseMessage, T> mapFunction) {
        return responses -> mapFunction.apply(responses.getLast());
    }
}
