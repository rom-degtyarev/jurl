package ru.jurl.http;

import org.junit.Test;
import ru.jurl.http.headers.ContentDisposition;
import ru.jurl.http.headers.ContentType;

import java.util.List;

import static org.junit.Assert.*;
import static ru.jurl.http.headers.ContentType.TEXT_PLAIN_UTF_8;

public class MultipartContentTest {

    @Test
    public void print_multipart_content() {
        MultipartContent multipart = new MultipartContent(
                "123",
                List.of(
                        new MultipartContent.BodyPart(
                                new ContentDisposition("form-data; name=\"field1\""),
                                null,
                                "value1".getBytes()
                        ),
                        new MultipartContent.BodyPart(
                                new ContentDisposition("form-data; name=\"field2\"; filename=\"test.txt\""),
                                TEXT_PLAIN_UTF_8,
                                "value2".getBytes()
                        )
                ));

        assertEquals(2, multipart.parts().size());
        assertFalse(multipart.parts().getFirst().isFile());
        assertTrue(multipart.parts().get(1).isFile());
        assertEquals("""
                --123
                Content-Disposition: form-data; name="field1"
                
                value1
                --123
                Content-Disposition: form-data; name="field2"; filename="test.txt"
                Content-Type: text/plain; charset=UTF-8
                
                value2
                --123--""", multipart.toString());
    }
}