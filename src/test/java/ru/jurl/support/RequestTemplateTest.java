package ru.jurl.support;

import org.junit.Test;
import ru.jurl.http.Parameter;
import ru.jurl.http.RequestMessage;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.jurl.support.Messages.merge;
import static ru.jurl.support.Messages.request;
import static ru.jurl.support.templates.Templates.hasPlaceholder;

public class RequestTemplateTest {

    @Test
    public void merge_message_template() {
        RequestMessage message = request("""
                POST http://${host}/api?param1=${param1}&param2=${param2}
                Content-Type: application/json
                
                < classpath:test.json
                """);
        RequestMessage merged = merge(message, Map.of(
                "host", new Parameter("host", () -> "localhost:8080"),
                "param1", new Parameter("param1", () -> "aaa"),
                "param2", new Parameter("param2", () -> "bbb"),
                "body-param", new Parameter("body-param", () -> "ccc")
        ));

        assertEquals("http://localhost:8080/api?param1=aaa&param2=bbb", merged.getRequestTarget());
        assertEquals("""
                {
                  "data": "test",
                  "success": true,
                  "body-param": "ccc"
                }""", merged.getBody().toString());
    }

    @Test
    public void hasPlaceholderTest() {
        assertTrue(hasPlaceholder("< path:/files/test.json"));
        assertTrue(hasPlaceholder("POST http://${host}/api?param1=${param1}&param2=${param2}"));
        assertTrue(hasPlaceholder("${param2}"));
    }
}