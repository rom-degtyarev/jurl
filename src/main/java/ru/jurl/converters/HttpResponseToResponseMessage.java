package ru.jurl.converters;

import ru.jurl.http.*;
import ru.jurl.http.headers.ContentType;
import ru.jurl.support.Headers;

import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static ru.jurl.http.ProtocolVersion.HTTP_1_1;
import static ru.jurl.http.ProtocolVersion.HTTP_2;
import static ru.jurl.http.headers.ContentType.TEXT_PLAIN_UTF_8;
import static ru.jurl.support.Bodies.valueOf;
import static ru.jurl.support.Headers.CONTENT_TYPE;

public class HttpResponseToResponseMessage implements Function<HttpResponse<byte[]>, ResponseMessage> {
    @Override
    public ResponseMessage apply(HttpResponse<byte[]> httpResponse) {
        List<Header> headers = cast(httpResponse.headers());
        Optional<ContentType> contentType = Headers.getFirst(headers, CONTENT_TYPE);
        Body body = valueOf(httpResponse.body(), contentType.orElse(TEXT_PLAIN_UTF_8));
        return ResponseMessage
                .create()
                .withProtocol(cast(httpResponse.version()))
                .withStatus(Status.of(httpResponse.statusCode()))
                .withHeaders(headers)
                .withBody(body)
                .please();
    }

    private static List<Header> cast(HttpHeaders headers) {
        return headers
                .map()
                .entrySet()
                .stream()
                .flatMap(header ->
                        header
                                .getValue()
                                .stream()
                                .map(headerValue -> Headers.valueOf(header.getKey(), headerValue))
                ).toList();
    }

    private static ProtocolVersion cast(HttpClient.Version version) {
        return switch (version) {
            case HTTP_2 -> HTTP_2;
            default -> HTTP_1_1;
        };
    }
}
