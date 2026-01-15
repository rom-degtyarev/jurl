package ru.jurl.http;

import lombok.SneakyThrows;
import ru.jurl.converters.HttpResponseToResponseMessage;
import ru.jurl.converters.RequestMessageToHttpRequest;

import java.net.CookieManager;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;

import static java.net.CookiePolicy.ACCEPT_ALL;
import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofByteArray;

public class HttpClientExchange implements Exchange {
    private final HttpClient httpClient;
    private final Function<RequestMessage, HttpRequest> requestConverter = new RequestMessageToHttpRequest();
    private final Function<HttpResponse<byte[]>, ResponseMessage> responseConverter = new HttpResponseToResponseMessage();

    public HttpClientExchange(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpClientExchange() {
        this(
                newBuilder()
                        .cookieHandler(new CookieManager(null, ACCEPT_ALL))
                        .build()
        );
    }

    @SneakyThrows
    @Override
    public ResponseMessage apply(RequestMessage requestMessage) {
        HttpRequest request = requestConverter.apply(requestMessage);
        HttpResponse<byte[]> response = httpClient.send(request, ofByteArray());
        return responseConverter.apply(response);
    }
}
