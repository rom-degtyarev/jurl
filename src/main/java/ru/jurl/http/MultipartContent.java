package ru.jurl.http;

import lombok.Builder;
import ru.jurl.http.headers.ContentDisposition;
import ru.jurl.http.headers.ContentType;

import java.util.List;

import static java.util.stream.Collectors.joining;
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
            StringBuilder text = new StringBuilder()
                    .append(contentDisposition).append("\n");
            if (contentType != null) {
                text.append(contentType).append("\n");
            }
            text.append("\n").append(getValueAsString());
            return text.toString();
        }

        public String getValueAsString() {
            if (contentType != null) {
                if (!isFile() || contentType.isText()) {
                    return new String(content, contentType.getCharset());
                } else {
                    return toHexString(content);
                }
            } else {
                if (isFile()) {
                    return toHexString(content);
                } else {
                    return new String(content, DEFAULT_CHARSET);
                }
            }
        }

        public boolean isFile() {
            return contentDisposition != null && !isEmpty(contentDisposition.getFileName());
        }
    }
}
