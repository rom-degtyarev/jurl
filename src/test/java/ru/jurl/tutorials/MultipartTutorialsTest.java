package ru.jurl.tutorials;

import lombok.SneakyThrows;
import org.junit.Test;
import ru.jurl.http.RequestMessage;
import ru.jurl.http.ResponseMessage;

import java.nio.file.Paths;
import java.util.Map;

import static java.nio.file.Files.readAllBytes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.jurl.JUrl.jurl;
import static ru.jurl.support.Messages.*;
import static ru.jurl.support.Strings.toHexString;

public class MultipartTutorialsTest {
    @SneakyThrows
    @Test
    //todo
    public void read_multipart_response() {
        // given
        byte[] content = readAllBytes(Paths.get("src/test/resources/IntelliJIDEA_ReferenceCard.pdf"));

        // when
        ResponseMessage reply = jurl(conversation -> conversation
                .withExchange(rq -> response("""
                HTTP/1.1 200 OK
                content-type: multipart/form-data; boundary=123
                
                --123
                Content-Disposition: form-data; name="file-attachment"; filename="test.pdf"
                Content-Type: application/pdf
                
                ${binary-content}
                --123--
                """))
        ).andThen(request("GET http://my-service/api"))
                .fetch();

        // then
        assertTrue(reply.getStatus().isOk());
    }

    @SneakyThrows
    @Test
    public void post_multipart_request() {
        // given
        byte[] content = readAllBytes(Paths.get("src/test/resources/IntelliJIDEA_ReferenceCard.pdf"));

        // when
        ResponseMessage response = jurl(conversation -> conversation
                .withParameter("binary-content", toHexString(content))
                .withExchange(rq -> response("HTTP/1.1 200 OK"))
        ).andThen(
                request("""
                        POST http://my-service/api
                        content-type: multipart/form-data; boundary=123
                        
                        --123
                        Content-Disposition: form-data; name="file-attachment"; filename="test.pdf"
                        Content-Type: application/pdf
                        
                        ${binary-content}
                        --123--"""
                )
        ).fetch();

        // then
        assertTrue(response.getStatus().isOk());
    }

    @Test
    public void parse_request_test() {
        RequestMessage message = request("""
                POST http://my-service/api
                content-type: multipart/form-data; boundary=123
                
                --123
                Content-Disposition: form-data; name="file-attachment"; filename="test.pdf"
                Content-Type: application/pdf
                
                ${binary-content}
                --123--""");

        assertEquals("""
                --123
                Content-Disposition: form-data; name="file-attachment"; filename="test.pdf"
                Content-Type: application/pdf
                
                ${binary-content}
                --123--""", message.getBody().toString());
    }
}
