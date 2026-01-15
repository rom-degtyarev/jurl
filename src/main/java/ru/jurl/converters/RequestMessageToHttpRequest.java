package ru.jurl.converters;

import lombok.SneakyThrows;
import ru.jurl.http.Header;
import ru.jurl.http.ProtocolVersion;
import ru.jurl.http.RequestMessage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.function.Function;

import static java.net.http.HttpClient.Version.HTTP_1_1;
import static java.net.http.HttpClient.Version.HTTP_2;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.BodyPublishers.ofByteArray;

public class RequestMessageToHttpRequest implements Function<RequestMessage, HttpRequest> {
    @SneakyThrows
    @Override
    public HttpRequest apply(RequestMessage requestMessage) {
        HttpRequest.Builder result = HttpRequest.newBuilder();
        result.uri(new URI(requestMessage.getRequestTarget()));
        for (Header header : requestMessage.getHeaders()) {
            result.header(header.getName(), header.getValue());
        }
        HttpClient.Version version = requestMessage.getProtocol() == ProtocolVersion.HTTP_2 ? HTTP_2 : HTTP_1_1;
        result.version(version);
        if (requestMessage.getBody() != null)
            result.method(
                    requestMessage.getMethod().name(),
                    ofByteArray(requestMessage.getBody().content())
            );
        else
            result.method(
                    requestMessage.getMethod().name(),
                    noBody()
            );
        return result.build();
    }
}
