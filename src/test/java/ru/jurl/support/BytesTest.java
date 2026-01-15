package ru.jurl.support;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BytesTest {
    @Test
    public void split_multipart() {
        List<byte[]> list = Bytes.split("""
                        --123
                        Content-Disposition: form-data; name="field1"
                        
                        value1
                        --123
                        Content-Disposition: form-data; name="field2"; filename="test.txt"
                        Content-Type: text/plain; charset=UTF-8
                        
                        value2
                        --123--""".getBytes(),
                "--123".getBytes());

        assertEquals(3, list.size());
        assertEquals("""
                Content-Disposition: form-data; name="field1"
                        
                value1""", new String(list.get(0)).trim()
        );
        assertEquals("""
                Content-Disposition: form-data; name="field2"; filename="test.txt"
                Content-Type: text/plain; charset=UTF-8
                
                value2""", new String(list.get(1)).trim());
        assertEquals("--", new String(list.get(2)).trim());
    }

    @Test
    public void split_bodypart() {
        List<byte[]> chunks = Bytes.split("""
                Content-Disposition: form-data; name="field2"; filename="test.txt"
                Content-Type: text/plain; charset=UTF-8
                
                value2""".getBytes(), "\n".getBytes(), false);

        List<String> list = chunks.stream().map(String::new).toList();
        assertEquals(4, list.size());
    }
}