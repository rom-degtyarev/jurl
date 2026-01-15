package ru.jurl.http;

import ru.jurl.converters.BytesToMultipartContent;
import ru.jurl.http.headers.ContentType;
import ru.jurl.support.Require;

import static ru.jurl.support.Strings.toHexString;

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
        if (contentType.isText() || contentType.isFormUrlEncoded()) {
            return new String(content, contentType.getCharset());
        }
        if (contentType.isMultipart()) {
            return getMultipartContent().toString();
        }
        return toHexString(content);
    }
}
