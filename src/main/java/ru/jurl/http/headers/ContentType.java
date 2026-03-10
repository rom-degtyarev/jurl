package ru.jurl.http.headers;

import lombok.Getter;
import ru.jurl.http.Header;
import ru.jurl.support.Tokenizer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static ru.jurl.support.Headers.CONTENT_TYPE_HEADER;
import static ru.jurl.support.Messages.*;
import static ru.jurl.support.Strings.isEmpty;

@Getter
public class ContentType extends Header {
    public static final ContentType TEXT_PLAIN_UTF_8 = new ContentType("text/plain; charset=UTF-8");

    private final String contentType;
    private final Charset charset;
    private final String boundary;

    public ContentType(String value) {
        super(CONTENT_TYPE_HEADER, value);
        Tokenizer tokens = new Tokenizer(value, ";");
        contentType = tokens.firstItem().trim();
        Map<String, String> meta = tokens.toMap();
        charset = isText() ? charsetOf(meta.get("charset")) : null;
        boundary = unquoted(meta.get("boundary"));
    }

    public Charset getCharsetOrDefault() {
        return charset == null ? DEFAULT_CHARSET : charset;
    }

    public boolean isText() {
        if (isEmpty(contentType)) return false;
        if (contentType.equalsIgnoreCase("application/json")) return true;
        return contentType.startsWith("text/");
    }

    public boolean isFormUrlEncoded() {
        return "application/x-www-form-urlencoded".equalsIgnoreCase(contentType);
    }

    public boolean isMultipart() {
        return "multipart/form-data".equalsIgnoreCase(contentType);
    }
}
