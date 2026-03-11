package ru.jurl.tutorials;

import lombok.SneakyThrows;
import org.junit.Test;
import ru.jurl.http.MultipartContent;
import ru.jurl.http.RequestMessage;
import ru.jurl.http.ResponseMessage;

import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.jurl.JUrl.jurl;
import static ru.jurl.http.Parameter.parameter;
import static ru.jurl.support.Messages.request;
import static ru.jurl.support.Messages.response;
import static ru.jurl.support.Strings.toHexString;
import static ru.jurl.support.templates.Templates.string;

public class MultipartTutorialsTest {

    @SneakyThrows
    @Test
    public void post_multipart_request() {
        // given
        byte[] content = readAllBytes(Paths.get("src/test/resources/IntelliJIDEA_ReferenceCard.pdf"));

        // when
        ResponseMessage response = jurl(conversation -> conversation
                .withParameter("binary-content", toHexString(content))
                .withExchange(rq -> response("HTTP/1.1 200 OK"))
        ).andThen("""
                POST http://my-service/api
                content-type: multipart/form-data; boundary=123
                
                --123
                Content-Disposition: form-data; name="file-attachment"; filename="test.pdf"
                Content-Type: application/pdf
                
                ${binary-content}
                --123--"""
        ).fetch();

        // then
        assertTrue(response.getStatus().isOk());
    }

    @SneakyThrows
    @Test
    public void read_multipart_response() {
        // given
        byte[] content = readAllBytes(Paths.get("src/test/resources/IntelliJIDEA_ReferenceCard.pdf"));

        ResponseMessage response = response(
                string("""
                        HTTP/1.1 200 OK
                        content-type: multipart/form-data; boundary=123
                        
                        --123
                        Content-Disposition: form-data; name="file-attachment"; filename="test.pdf"
                        Content-Type: application/pdf
                        
                        ${binary-content}
                        --123--
                        """
                ).merge(parameter("binary-content", toHexString(content)))
        );

        // when
        ResponseMessage reply = jurl(conversation -> conversation
                .withExchange(rq -> response)
        ).andThen("GET http://my-service/api")
                .fetch();

        // then
        assertTrue(reply.getStatus().isOk());
        MultipartContent.BodyPart bodyPart = reply.getBody().getMultipartContent().parts().get(0);
        assertEquals(toHexString(content), toHexString(bodyPart.content()));
    }

    @SneakyThrows
    @Test
    public void parse_request_test() {
        String hexString = toHexString(
                readAllBytes(Paths.get("src/test/resources/IntelliJIDEA_ReferenceCard.pdf"))
        );

        String text = """
                POST http://my-service/api
                content-type: multipart/form-data; boundary=123
                
                --123
                Content-Disposition: form-data; name="file-attachment"; filename="test.pdf"
                Content-Type: application/pdf
                
                ${binary-content}
                --123--""";

        String merged = string(text).merge(parameter("binary-content", hexString));
        RequestMessage message = request(merged);

        MultipartContent.BodyPart bodyPart = message.getBody().getMultipartContent().parts().get(0);
        assertEquals(hexString, toHexString(bodyPart.content()));
    }
}
