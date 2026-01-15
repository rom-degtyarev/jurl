package ru.jurl.http;

import lombok.SneakyThrows;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static ru.jurl.support.Messages.request;

public class HttpClientExchangeTest {
    @Test
    public void http_get_html_request() {
        Exchange exchange = new HttpClientExchange();
        ResponseMessage response = exchange.apply(request("GET https://ya.ru/"));
        assertTrue(response.getStatus().isOk());
    }

    @SneakyThrows
    @Test
    public void http_get_binary_request() {
        Exchange exchange = new HttpClientExchange();
        ResponseMessage response = exchange.apply(request("GET https://yastatic.net/s3/home-static/_/nova/G1Rt0YW3.png"));
        assertTrue(response.getStatus().isSuccess());
    }
}