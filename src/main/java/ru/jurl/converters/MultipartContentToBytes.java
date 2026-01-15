package ru.jurl.converters;

import ru.jurl.http.MultipartContent;

import java.io.ByteArrayOutputStream;
import java.util.function.Function;

public class MultipartContentToBytes implements Function<MultipartContent, byte[]> {
    @Override
    public byte[] apply(MultipartContent multipartContent) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        multipartContent
                .parts()
                .stream()
                .map(chunk -> toBytes(multipartContent.boundary(), chunk))
                .forEach(out::writeBytes);
        out.writeBytes(boundaryBytes(multipartContent.boundary(), "--"));
        return out.toByteArray();
    }

    private byte[] toBytes(String boundary, MultipartContent.BodyPart bodyPart) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.writeBytes(boundaryBytes(boundary, "\n"));
        if (bodyPart.contentType() != null) {
            out.writeBytes(bodyPart.contentType().toString().getBytes());
            out.write('\n');
        }
        if (bodyPart.contentDisposition() != null) {
            out.writeBytes(bodyPart.contentDisposition().toString().getBytes());
            out.write('\n');
        }
        out.write('\n');
        out.writeBytes(bodyPart.content());
        return out.toByteArray();
    }

    private static byte[] boundaryBytes(String boundary, String suffix) {
        return ("--" + boundary + suffix).getBytes();
    }
}
