package ru.jurl.http;

import ru.jurl.converters.BytesToMultipartContent;
import ru.jurl.http.headers.ContentType;
import ru.jurl.support.Bodies;
import ru.jurl.support.Require;

public record Body(
        ContentType contentType,
        byte[] content
) {
    public MultipartContent getMultipartContent() {
        Require.isTrue(
                contentType.isMultipart(),
                () -> "Message body [Content-Type: multipart/form-data] expected, but actual is " + contentType.getName()
        );
        return new BytesToMultipartContent(contentType).apply(content);
    }

    @Override
    public String toString() {
        return Bodies.toString(this);
    }
}
