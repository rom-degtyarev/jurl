package ru.jurl.http;

import lombok.Builder;
import ru.jurl.http.headers.ContentDisposition;
import ru.jurl.http.headers.ContentType;
import ru.jurl.support.Bodies;
import ru.jurl.support.MessageTemplate;

import java.nio.charset.Charset;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static ru.jurl.support.MessageTemplate.hasPlaceholder;
import static ru.jurl.support.Messages.DEFAULT_CHARSET;
import static ru.jurl.support.Strings.isEmpty;
import static ru.jurl.support.Strings.toHexString;

public record MultipartContent(
        String boundary,
        List<BodyPart> parts
) {
    @Override
    public String toString() {
        String text = parts
                .stream()
                .map(part -> "--" + boundary + "\n" + part)
                .collect(joining("\n"));
        return text + "\n--" + boundary + "--";
    }

    @Builder(
            setterPrefix = "with",
            builderMethodName = "create",
            buildMethodName = "please"
    )
    public record BodyPart(
            ContentDisposition contentDisposition,
            ContentType contentType,
            byte[] content
    ) {
        @Override
        public String toString() {
            return Bodies.toString(this);
        }

        public String getValueAsString() {
            Charset charset = contentType == null ? DEFAULT_CHARSET : contentType.getCharsetOrDefault();
            String stringContent = new String(content, charset);
            if (hasPlaceholder(stringContent))
                return stringContent;
            if (contentType != null) {
                if (!isFile() || contentType.isText()) {
                    return stringContent;
                } else {
                    return toHexString(content);
                }
            } else {
                if (isFile()) {
                    return toHexString(content);
                } else {
                    return stringContent;
                }
            }
        }

        public boolean isFile() {
            return contentDisposition != null && !isEmpty(contentDisposition.getFileName());
        }
    }
}
