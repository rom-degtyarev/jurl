package ru.jurl.http;

import java.util.List;

@FunctionalInterface
public interface Extractor<T> {
    T extract(List<ResponseMessage> responseMessages);
}
