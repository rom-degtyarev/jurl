package ru.jurl;

import org.junit.Test;
import ru.jurl.http.Exchange;
import ru.jurl.http.MultipartContent;
import ru.jurl.http.ResponseMessage;

import static org.junit.Assert.*;
import static ru.jurl.JUrl.jurl;
import static ru.jurl.support.Messages.response;
import static ru.jurl.support.Strings.toHexString;

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
                .withParameters("yandex", "https://yandex.ru")
        ).andThen("GET ${yandex}/metro")
                .andThen("GET ${yandex}/images")
                .andThen("GET ${yandex}/maps")
                .run();

        assertTrue(response.getStatus().isSuccess() || response.getStatus().isRedirected());
    }

    @Test
    public void http_response_mocking() {
        //given
        Exchange mockExchange = request ->
                response(
                        """
                                HTTP/1.1 200 OK
                                Content-Type: multipart/form-data; boundary=123
                                
                                --123
                                Content-Disposition: form-data; name="field1"
                                
                                value1
                                --123
                                Content-Disposition: form-data; name="field2"; filename="test.pdf"
                                Content-Type: application/pdf
                                
                                %s
                                --123--""".formatted(toHexString("BINARY CONTENT"))
                );
        //when
        ResponseMessage response = jurl(conversation -> conversation
                .withExchange(mockExchange)
        ).andThen("GET https://ya.ru/")
                .run();
        //then
        assertTrue(response.getStatus().isOk());
        assertTrue(response.getBody().contentType().isMultipart());
        MultipartContent multipart = response.getBody().getMultipartContent();
        assertEquals("value1", multipart.parts().getFirst().getValueAsString());
        MultipartContent.BodyPart filePart = multipart.parts().getLast();
        assertEquals(toHexString("BINARY CONTENT"), filePart.getValueAsString());
    }
}