package ru.jurl.support;

import ru.jurl.http.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.jurl.support.Headers.getFirst;

public class Extractors {
    public static <T> Extractor<T> Last(Function<ResponseMessage, T> mapFunction) {
        return responses -> mapFunction.apply(responses.getLast());
    }

    public static Function<ResponseMessage, String> ResponseHeader(String headerName) {
        return response ->
                getFirst(
                        response.getHeaders(),
                        header -> header.getName().equalsIgnoreCase(headerName)
                ).map(Header::getValue).orElse("");
    }

    public static Function<ResponseMessage, String> BodyText() {
        return response -> response.getBody().toString();
    }

    public static Function<ResponseMessage, String> BodyText(String groupName, String regExp) {
        return response -> {
            Require.notNull(response.getBody(), () -> "Response body is null");
            Require.isTrue(response.getBody().contentType().isText(), () -> "Response body text content expected");
            String text = response.getBody().toString();
            Pattern pattern = Pattern.compile(regExp);
            Matcher matcher = pattern.matcher(text);
            return matcher.find() ? matcher.group(groupName) : "";
        };
    }

    public static Function<ResponseMessage, Optional<MultipartContent.BodyPart>> Attachment() {
        return response -> {
            Body body = response.getBody();
            Require.notNull(body, () -> "Response body is null");
            MultipartContent multipart = body.getMultipartContent();
            return multipart
                    .parts()
                    .stream()
                    .filter(MultipartContent.BodyPart::isFile)
                    .findFirst();
        };
    }
}
