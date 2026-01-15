package ru.jurl.support;

import org.junit.Test;
import ru.jurl.http.ResponseMessage;

import static org.junit.Assert.*;
import static ru.jurl.http.ProtocolVersion.HTTP_1_1;
import static ru.jurl.support.Messages.response;
import static ru.jurl.support.Strings.toHexString;

public class ResponseMessagesTest {
    @Test
    public void read_text_response_message() {
        ResponseMessage response = response("""
                HTTP/1.1 200 OK
                Content-Type: application/json
                Content-Length: 49
                
                { 
                    "data": "123" 
                }
                """);

        assertEquals(HTTP_1_1, response.getProtocol());
        assertEquals("""
                { 
                    "data": "123" 
                }""", response.getBody().toString());
        assertEquals(2, response.getHeaders().size());
        assertTrue(response.getBody().contentType().isText());
    }

    @Test
    public void read_binary_response_message() {
        ResponseMessage response = response("""
                HTTP/1.1 200 OK
                Content-Type: application/pdf
                Content-Length: 49
                
                """ + toHexString("PDF CONTENT"));

        assertEquals(HTTP_1_1, response.getProtocol());
        assertEquals(toHexString("PDF CONTENT"), response.getBody().toString());
        assertEquals(2, response.getHeaders().size());
        assertFalse(response.getBody().contentType().isText());
    }
}
