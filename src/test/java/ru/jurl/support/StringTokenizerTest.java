package ru.jurl.support;

import org.junit.Test;

import static org.junit.Assert.*;
import static ru.jurl.support.Strings.*;

public class StringTokenizerTest {

    public static final String HTTP_REQUEST = """
            POST http://localhost/api/ HTTP/1.1
            Content-Type: application/json
            
            { 
                "data": "123" 
            }
            """;

    @Test
    public void split_http_message_headers_and_body() {
        Tokenizer tokenizer = emptyLineTokenizer(HTTP_REQUEST);
        assertEquals(
                """
            POST http://localhost/api/ HTTP/1.1
            Content-Type: application/json""",
                tokenizer.firstItem()
        );
        assertEquals(
                """
                        { 
                            "data": "123" 
                        }
                        """,
                tokenizer.nextItem()
        );
    }

    @Test
    public void split_text_to_lines() {
        Tokenizer httpTokenizer = emptyLineTokenizer(HTTP_REQUEST);
        Tokenizer headers = lineTokenizer(httpTokenizer.firstItem());
        String body = httpTokenizer.nextItem();

        assertEquals("POST http://localhost/api/ HTTP/1.1", headers.nextItem());
        assertEquals("Content-Type: application/json", headers.nextItem());
        assertEquals("""
                { 
                    "data": "123" 
                }
                """, body);
    }

    @Test
    public void split_empty_text() {
        Tokenizer tokenizer = lineTokenizer(null);
        assertNull(tokenizer.nextItem());
    }

    @Test
    public void filter_lines() {
        Tokenizer tokenizer = lineTokenizer("""
                POST http://localhost/api/ HTTP/1.1
                # filter line 1
                Content-Type: application/json
                
                # filter line 2
                { "data": "123" }"""
        ).filter(line -> !line.startsWith("#"));

        assertEquals("""
                POST http://localhost/api/ HTTP/1.1
                Content-Type: application/json
                
                { "data": "123" }""", tokenizer.toString());
    }

    @Test
    public void read_first_line() {
        Tokenizer tokenizer = lineTokenizer(HTTP_REQUEST);

        assertEquals("POST http://localhost/api/ HTTP/1.1", tokenizer.firstItem());
    }

    @Test
    public void read_first_line_tokens() {
        Tokenizer message = lineTokenizer(HTTP_REQUEST);
        Tokenizer tokenizer = wordTokenizer(message.firstItem());

        assertEquals("POST", tokenizer.nextItem());
        assertEquals("http://localhost/api/", tokenizer.nextItem());
        assertEquals("HTTP/1.1", tokenizer.nextItem());
        assertNull(tokenizer.nextItem());
    }
}
