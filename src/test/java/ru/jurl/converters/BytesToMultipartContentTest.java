package ru.jurl.converters;

import org.junit.Test;
import ru.jurl.http.MultipartContent;
import ru.jurl.http.headers.ContentType;

import static org.junit.Assert.assertEquals;

public class BytesToMultipartContentTest {
    @Test
    public void convert_multipart_body_content() {
        byte[] bytes = """
                --123
                Content-Disposition: form-data; name="field1"
                
                value1
                --123
                Content-Disposition: form-data; name="field2"; filename="test.txt"
                Content-Type: text/plain; charset=UTF-8
                
                value2
                --123--""".getBytes();
        MultipartContent multipart = new BytesToMultipartContent(
                new ContentType("multipart/form-data; boundary=123")
        ).apply(bytes);

        assertEquals(2, multipart.parts().size());

        MultipartContent.BodyPart first = multipart.parts().getFirst();
        assertEquals("value1", first.getValueAsString());

        MultipartContent.BodyPart second = multipart.parts().get(1);
        assertEquals("value2", second.getValueAsString());
    }
}