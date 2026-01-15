package ru.jurl.http.headers;

import org.junit.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContentTypeTest {
    @Test
    public void content_types_test() {
        assertTrue(new ContentType("text/html; charset=utf-8").isText());
        assertTrue(new ContentType("multipart/form-data; boundary=BoundaryString").isMultipart());
        assertTrue(new ContentType("application/x-www-form-urlencoded").isFormUrlEncoded());
    }

    @Test
    public void charset_test() {
        assertEquals(UTF_8, new ContentType("text/html; charset='utf-8'").getCharset());
        assertEquals(UTF_8, new ContentType("text/html; charset=utf-8").getCharset());
        assertEquals(UTF_8, new ContentType("text/html; charset=\"UTF-8\"").getCharset());
    }
}