package ru.jurl.converters;

import ru.jurl.http.Header;
import ru.jurl.http.MultipartContent;
import ru.jurl.http.headers.ContentType;
import ru.jurl.support.Headers;
import ru.jurl.support.Tokenizer;

import static ru.jurl.http.headers.ContentType.TEXT_PLAIN_UTF_8;
import static ru.jurl.support.Bodies.toBytes;
import static ru.jurl.support.Bodies.valueOf;
import static ru.jurl.support.Strings.*;

public record StringToHttpMessageParser(
        HttpMessageConsumer consumer,
        boolean skipFirstLine
) {
    public void parse(String message) {
        Tokenizer tokenizer = emptyLineTokenizer(message);
        Tokenizer headerLines = lineTokenizer(tokenizer.firstItem());
        if (!skipFirstLine) {
            Tokenizer startLine = wordTokenizer(headerLines.firstItem());

            consumer.onStartLine(startLine.toList());
        }

        String headerLine;
        ContentType contentType = TEXT_PLAIN_UTF_8;
        while ((headerLine = headerLines.nextItem()) != null) {
            Header header = Headers.valueOf(headerLine);
            if (header instanceof ContentType) {
                contentType = (ContentType) header;
            }
            consumer.onHeader(header);
        }

        String bodyLine = tokenizer.getRest("\n\n");
        if (bodyLine != null) {
            byte[] content = toBytes(bodyLine.trim(), contentType);
            consumer.onBodyContent(valueOf(content, contentType));
        }
    }
}
