package ru.jurl.support;

import ru.jurl.converters.MultipartContentToBytes;
import ru.jurl.converters.StringToMultipartContent;
import ru.jurl.http.Body;
import ru.jurl.http.MultipartContent;
import ru.jurl.http.headers.ContentType;

import static ru.jurl.support.MessageTemplate.hasPlaceholder;
import static ru.jurl.support.Strings.fromHexString;
import static ru.jurl.support.Strings.toHexString;

public interface Bodies {
    static Body valueOf(String content, ContentType contentType) {
        return new Body(contentType, content.getBytes(contentType.getCharsetOrDefault()));
    }

    static Body valueOf(byte[] content, ContentType contentType) {
        return new Body(contentType, content);
    }

    static String toString(Body body) {
        if (body.contentType().isText() || body.contentType().isFormUrlEncoded()) {
            return new String(body.content(), body.contentType().getCharsetOrDefault());
        }
        if (body.contentType().isMultipart()) {
            return body.getMultipartContent().toString();
        }
        return toHexString(body.content());
    }

    static String toString(MultipartContent.BodyPart bodyPart) {
        StringBuilder text = new StringBuilder()
                .append(bodyPart.contentDisposition()).append("\n");
        if (bodyPart.contentType() != null) {
            text.append(bodyPart.contentType()).append("\n");
        }
        text.append("\n").append(bodyPart.getValueAsString());
        return text.toString();
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
