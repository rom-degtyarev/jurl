package ru.jurl;

import org.junit.Test;
import ru.jurl.http.ResponseMessage;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.jurl.JUrl.jurl;

public class JUrlTest {
    @Test
    public void jurl_conversation() {
        ResponseMessage response = jurl("GET https://ya.ru/").run();
        System.out.println(response.getStatus());
        assertFalse(response.getStatus().isError());
    }

    @Test
    public void jurl_parameterized_conversation() {
        ResponseMessage response = jurl(conversation -> conversation
                .withParameters(Map.of(
                        "yandex", () -> "https://yandex.ru")
                )
        ).andThen("GET ${yandex}/metro")
                .andThen("GET ${yandex}/images")
                .andThen("GET ${yandex}/maps")
                .run();

        assertTrue(response.getStatus().isSuccess() || response.getStatus().isRedirected());
    }
}