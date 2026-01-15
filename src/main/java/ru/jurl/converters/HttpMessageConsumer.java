package ru.jurl.converters;

import ru.jurl.http.Body;
import ru.jurl.http.Header;

import java.util.List;

public interface HttpMessageConsumer {
    void onStartLine(List<String> startLineItems);
    void onHeader(Header header);
    void onBodyContent(Body body);
}
