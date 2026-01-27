package ru.jurl.support;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import ru.jurl.http.Extractor;
import ru.jurl.http.MultipartContent.BodyPart;
import ru.jurl.http.ResponseMessage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static ru.jurl.support.Extractors.*;
import static ru.jurl.support.Messages.response;

public class ExtractorsTest {

    @Test
    public void get_last_response_fails() {
        //given
        Extractor<ResponseMessage> last = Last(response -> response);
        //when
        ThrowingRunnable extractEmptyResponse = () -> last.extract(emptyList());
        //then
        assertThrows(NoSuchElementException.class, extractEmptyResponse);
    }

    @Test
    public void get_last_response() {
        //given
        List<ResponseMessage> messages = List.of(response("""
                HTTP/1.1 200 OK
                
                expected""")
        );
        //when
        Extractor<ResponseMessage> last = Last(response -> response);
        String actualValue = last.extract(messages).getBody().toString();
        //then
        assertEquals("expected", actualValue);
    }

    @Test
    public void get_last_response_header() {
        //given
        List<ResponseMessage> messages = List.of(response("""
                HTTP/1.1 200 OK
                Content-Type: application/json""")
        );
        //when
        Extractor<String> extractor = Last(ResponseHeader("Content-Type"));
        String actualValue = extractor.extract(messages);
        //then
        assertEquals("application/json", actualValue);
    }

    @Test
    public void get_last_body_substring() {
        //given
        List<ResponseMessage> messages = List.of(response("""
                HTTP/1.1 200 OK
                Content-Type: text/html
                
                <html>
                    <body>
                        <form action="http://test.app:8080/api/login">
                        test
                        </form>
                    </body>
                </html>""")
        );
        //when
        Extractor<String> extractor = Last(BodyText("url", "action=\"(?<url>.*?)\""));
        String url = extractor.extract(messages);
        //then
        assertEquals("http://test.app:8080/api/login", url);
    }

    @Test
    public void get_last_attachment() {
        //given
        List<ResponseMessage> messages = List.of(response("""
                HTTP/1.1 200 OK
                Content-Type: multipart/form-data; boundary=123
                
                --123
                Content-Disposition: form-data; name="field1"
                
                value1
                --123
                Content-Disposition: form-data; name="field2"; filename="test.txt"
                Content-Type: text/plain; charset=UTF-8
                
                value2
                --123--""")
        );
        //when
        Extractor<Optional<BodyPart>> extractor = Last(Attachment());
        Optional<BodyPart> bodyPart = extractor.extract(messages);
        //then
        assertTrue(bodyPart.isPresent());
        BodyPart attachment = bodyPart.get();
        assertEquals("test.txt", attachment.contentDisposition().getFileName());
        assertEquals("field2", attachment.contentDisposition().getFieldName());
        assertEquals("value2", attachment.getValueAsString());
    }
}