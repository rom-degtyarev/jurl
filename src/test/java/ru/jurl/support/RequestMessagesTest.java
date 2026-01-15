package ru.jurl.support;

import org.junit.Test;
import ru.jurl.http.MultipartContent;
import ru.jurl.http.RequestMessage;

import static org.junit.Assert.*;
import static ru.jurl.http.Method.POST;
import static ru.jurl.http.ProtocolVersion.HTTP_1_1;
import static ru.jurl.support.Messages.request;
import static ru.jurl.support.Strings.toHexString;

public class RequestMessagesTest {
    @Test
    public void read_text_request_message() {
        RequestMessage request = request("""
                POST http://localhost/api/ HTTP/1.1
                Content-Type: application/json
                Content-Length: 49
                
                { 
                    "data": "123" 
                }
                """);

        assertEquals(HTTP_1_1, request.getProtocol());
        assertEquals("http://localhost/api/", request.getRequestTarget());
        assertEquals(POST, request.getMethod());
        assertEquals("""
                { 
                    "data": "123" 
                }""", request.getBody().toString());
        assertEquals(2, request.getHeaders().size());
        assertTrue(request.getBody().contentType().isText());
    }

    @Test
    public void read_binary_request_message() {
        RequestMessage request = request("""
                POST http://localhost/api/ HTTP/1.1
                Content-Type: application/pdf
                Content-Length: 49
                
                """ + toHexString("BINARY CONTENT"));

        assertEquals(HTTP_1_1, request.getProtocol());
        assertEquals("http://localhost/api/", request.getRequestTarget());
        assertEquals(POST, request.getMethod());
        assertEquals(toHexString("BINARY CONTENT"), request.getBody().toString());
        assertEquals(2, request.getHeaders().size());
        assertFalse(request.getBody().contentType().isText());
    }

    @Test
    public void read_multipart_request_message() {
        RequestMessage request = request("""
                POST http://localhost/api/ HTTP/1.1
                Content-Type: multipart/form-data; boundary=123
                
                --123
                Content-Disposition: form-data; name="field1"
                
                value1
                --123
                Content-Disposition: form-data; name="field2"; filename="test.pdf"
                Content-Type: application/pdf
                
                %s
                --123--""".formatted(toHexString("BINARY CONTENT")));

        assertEquals(HTTP_1_1, request.getProtocol());
        assertEquals("http://localhost/api/", request.getRequestTarget());
        assertEquals(POST, request.getMethod());
        assertTrue(request.getBody().contentType().isMultipart());

        MultipartContent multipart = request.getBody().getMultipartContent();
        assertEquals(2, multipart.parts().size());

        MultipartContent.BodyPart first = multipart.parts().getFirst();
        assertEquals("value1", first.getValueAsString());

        MultipartContent.BodyPart second = multipart.parts().getLast();
        assertTrue(second.isFile());
        assertEquals(toHexString("BINARY CONTENT"), second.getValueAsString());
    }
}