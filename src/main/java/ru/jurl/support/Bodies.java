package ru.jurl.support;

import ru.jurl.converters.MultipartContentToBytes;
import ru.jurl.converters.StringToMultipartContent;
import ru.jurl.http.Body;
import ru.jurl.http.MultipartContent;
import ru.jurl.http.headers.ContentType;

import static ru.jurl.support.Strings.fromHexString;
import static ru.jurl.support.templates.Templates.hasPlaceholder;

public interface Bodies {
    static Body valueOf(String content, ContentType contentType) {
        return new Body(contentType, content.getBytes(contentType.getCharsetOrDefault()));
    }

    static byte[] toBytes(String content, ContentType contentType) {
        if (contentType.isText() || hasPlaceholder(content)) {
            return content.getBytes(contentType.getCharsetOrDefault());
        } else if (contentType.isMultipart()) {
            MultipartContent multipart = new StringToMultipartContent(contentType.getBoundary()).apply(content);
            return new MultipartContentToBytes().apply(multipart);
        } else if (contentType.isFormUrlEncoded()) {
            return content.getBytes(contentType.getCharsetOrDefault());
        } else {
            return fromHexString(content);
        }
    }
}
