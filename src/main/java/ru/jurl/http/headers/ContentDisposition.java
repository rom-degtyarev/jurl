package ru.jurl.http.headers;

import lombok.Getter;
import ru.jurl.http.Header;
import ru.jurl.support.Tokenizer;

import java.util.Map;

import static ru.jurl.support.Headers.CONTENT_DISPOSITION_HEADER;
import static ru.jurl.support.Messages.unquoted;

@Getter
public class ContentDisposition extends Header {
    public static final String INLINE = "inline";
    public static final String ATTACHMENT = "attachment";
    public static final String FORM_DATA = "form-data";

    private final String type;
    private final String fieldName;
    private final String fileName;

    public ContentDisposition(String value) {
        super(CONTENT_DISPOSITION_HEADER, value);

        Tokenizer tokens = new Tokenizer(value, ";");
        type = tokens.firstItem().trim();
        Map<String, String> meta = tokens.toMap();
        fieldName = unquoted(meta.get("name"));
        fileName = unquoted(meta.get("filename"));
    }

    public boolean isFormData() {
        return FORM_DATA.equalsIgnoreCase(type);
    }
}
