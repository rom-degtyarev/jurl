package ru.jurl.converters;

import ru.jurl.http.Header;
import ru.jurl.http.MultipartContent;
import ru.jurl.http.headers.ContentDisposition;
import ru.jurl.http.headers.ContentType;
import ru.jurl.support.*;

import java.util.List;
import java.util.function.Function;

import static ru.jurl.support.Bytes.trim;
import static ru.jurl.support.Headers.CONTENT_DISPOSITION;
import static ru.jurl.support.Headers.CONTENT_TYPE;
import static ru.jurl.support.Messages.DEFAULT_CHARSET;

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
        List<byte[]> chunks = Bytes.split(trim(bytes), "\n".getBytes(), false);
        MultipartContent.BodyPart.BodyPartBuilder bodyPart = MultipartContent.BodyPart.create();
        boolean contentExpected = false;
        for (byte[] chunk : chunks) {
            if (contentExpected) {
                bodyPart.withContent(chunk);
                break;
            }
            if (chunk.length == 0) {
                contentExpected = true;
                continue;
            }
            String headerLine = new String(chunk, DEFAULT_CHARSET);
            Header header = Headers.valueOf(headerLine);
            if (CONTENT_TYPE.equalsIgnoreCase(header.getName())) {
                bodyPart.withContentType((ContentType) header);
            }
            if (CONTENT_DISPOSITION.equalsIgnoreCase(header.getName())) {
                bodyPart.withContentDisposition((ContentDisposition) header);
            }
        }
        return bodyPart.please();
    }
}
