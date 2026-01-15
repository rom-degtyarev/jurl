package ru.jurl.converters;

import ru.jurl.http.Body;
import ru.jurl.http.Header;
import ru.jurl.http.MultipartContent;
import ru.jurl.http.headers.ContentDisposition;
import ru.jurl.http.headers.ContentType;
import ru.jurl.support.Tokenizer;

import java.util.List;
import java.util.function.Function;

public class StringToMultipartContent implements Function<String, MultipartContent> {
    private final String boundary;

    public StringToMultipartContent(String boundary) {
        this.boundary = boundary;
    }

    @Override
    public MultipartContent apply(String bodyContent) {
        Tokenizer chunks = new Tokenizer(bodyContent, "--" + boundary);
        List<MultipartContent.BodyPart> parts = chunks
                .stream()
                .filter(chunk -> chunk.contains("\n"))
                .map(this::toBodyPart)
                .toList();
        return new MultipartContent(boundary, parts);
    }

    private MultipartContent.BodyPart toBodyPart(String chunk) {
        MultipartContent.BodyPart.BodyPartBuilder bodyPart = MultipartContent.BodyPart.create();

        StringToHttpMessageParser parser = new StringToHttpMessageParser(new HttpMessageConsumer() {
            @Override
            public void onStartLine(List<String> startLineItems) {}

            @Override
            public void onHeader(Header header) {
                if (header instanceof ContentType contentType) {
                    bodyPart.withContentType(contentType);
                } else if (header instanceof ContentDisposition contentDisposition) {
                    bodyPart.withContentDisposition(contentDisposition);
                }
            }

            @Override
            public void onBodyContent(Body body) {
                bodyPart.withContent(body.content());
            }
        }, true);
        parser.parse(chunk.trim());
        return bodyPart.please();
    }
}
