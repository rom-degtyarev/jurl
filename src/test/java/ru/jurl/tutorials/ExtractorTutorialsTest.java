package ru.jurl.tutorials;

import org.junit.Test;
import ru.jurl.http.Exchange;
import ru.jurl.http.MultipartContent;
import ru.jurl.http.ResponseMessage;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.jurl.JUrl.jurl;
import static ru.jurl.support.Extractors.*;
import static ru.jurl.support.Headers.CONTENT_TYPE;
import static ru.jurl.support.Messages.response;

public class ExtractorTutorialsTest {
    @Test
    public void extract_text_body() {
        // given suggest the response
        Exchange call = request -> response("""
                HTTP/1.1 200 OK
                Content-Type: text/plain
                
                some text
                """);

        // when
        String text = jurl(conversation ->
                conversation.withExchange(call)
        ).andThen("GET http://my-service/api?x=y")
                .fetch(Last(BodyText()));

        // then
        assertEquals("some text", text);
    }

    @Test
    public void extract_response_message() {
        // given suggest the response
        Exchange call = request -> response("""
                HTTP/1.1 200 OK
                Content-Type: text/plain
                
                some text
                """);

        // when
        ResponseMessage response = jurl(conversation ->
                conversation.withExchange(call)
        ).andThen("GET http://my-service/api?x=y")
                .fetch();

        // then
        assertTrue(response.getStatus().isOk());
        assertEquals("text/plain", response.getHeader(CONTENT_TYPE).getValue());
        assertEquals("some text", response.getBody().toString());
    }

    @Test
    public void extract_attachment() {
        // given suggest the response
        Exchange call = request -> response("""
                HTTP/1.1 200 OK
                Content-Type: multipart/form-data; boundary=123
                
                --123
                Content-Disposition: form-data; name="field1"
                
                value1
                --123
                Content-Disposition: form-data; name="field2"; filename="test.txt"
                Content-Type: text/plain; charset=UTF-8
                
                value2
                --123--""");

        // when
        Optional<MultipartContent.BodyPart> bodyPart = jurl(conversation ->
                conversation.withExchange(call)
        ).andThen("GET http://my-service/api?x=y")
                .fetch(Last(Attachment()));

        // then
        assertTrue(bodyPart.isPresent());
        MultipartContent.BodyPart attachment = bodyPart.get();
        assertEquals("test.txt", attachment.contentDisposition().getFileName());
        assertEquals("field2", attachment.contentDisposition().getFieldName());
        assertEquals("value2", attachment.getValueAsString());
    }
}
