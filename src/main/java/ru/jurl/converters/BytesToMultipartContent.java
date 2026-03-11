package ru.jurl.converters;

import ru.jurl.http.Header;
import ru.jurl.http.MultipartContent;
import ru.jurl.http.headers.ContentDisposition;
import ru.jurl.http.headers.ContentType;
import ru.jurl.support.*;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.copyOfRange;
import static ru.jurl.http.headers.ContentType.TEXT_PLAIN_UTF_8;
import static ru.jurl.support.Bytes.trim;
import static ru.jurl.support.Headers.CONTENT_DISPOSITION_HEADER;
import static ru.jurl.support.Headers.CONTENT_TYPE_HEADER;
import static ru.jurl.support.Messages.DEFAULT_CHARSET;
import static ru.jurl.support.Strings.isEmpty;
import static ru.jurl.support.Strings.lineTokenizer;

public record BytesToMultipartContent(
        ContentType contentType
) implements Function<byte[], MultipartContent> {

    @Override
    public MultipartContent apply(byte[] bytes) {
        Require.notNull(contentType, () -> "Required header [Content-Type: multipart/form-data]");
        Require.notNull(contentType.getBoundary(), () -> "Boundary attribute is null");

        List<byte[]> chunks = Bytes.split(bytes, ("--" + contentType.getBoundary()).getBytes());
        Require.isTrue(chunks.size() > 1, () -> "Invalid multipart/form-data content - chunks count not enough");
        List<MultipartContent.BodyPart> parts = chunks
                .subList(0, chunks.size() - 1)
                .stream()
                .map(this::toBodyPart)
                .toList();

        return new MultipartContent(contentType.getBoundary(), parts);
    }

    private MultipartContent.BodyPart toBodyPart(byte[] bytes) {
        byte[] emptyLine = "\n\n".getBytes();
        int indx = Bytes.indexOf(bytes, emptyLine);
        Require.isTrue(indx > 0, () -> "Invalid multipart/form-data content - empty chunk");
        byte[] headerBytes = trim(copyOfRange(bytes, 0, indx));
        byte[] contentBytes = copyOfRange(bytes, indx + emptyLine.length, bytes.length);
        MultipartContent.BodyPart.BodyPartBuilder bodyPart = MultipartContent.BodyPart.create();
        List<String> lines = lineTokenizer(new String(headerBytes, DEFAULT_CHARSET)).toList();
        ContentType contentType = TEXT_PLAIN_UTF_8;
        for (String headerLine : lines) {
            Header header = Headers.valueOf(headerLine);
            if (CONTENT_TYPE_HEADER.equalsIgnoreCase(header.getName())) {
                contentType = (ContentType) header;
                bodyPart.withContentType(contentType);
            }
            if (CONTENT_DISPOSITION_HEADER.equalsIgnoreCase(header.getName())) {
                bodyPart.withContentDisposition((ContentDisposition) header);
            }
        }
        if (contentType.isText()) {
            contentBytes = trim(contentBytes);
        }
        bodyPart.withContent(contentBytes);
        return bodyPart.please();
    }
}
