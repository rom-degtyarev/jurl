package ru.jurl.support;

import ru.jurl.converters.StringToRequestMessage;
import ru.jurl.converters.StringToResponseMessage;
import ru.jurl.http.Body;
import ru.jurl.http.Header;
import ru.jurl.http.RequestMessage;
import ru.jurl.http.ResponseMessage;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static ru.jurl.support.Strings.abbreviate;
import static ru.jurl.support.Strings.isEmpty;

public class Messages {
    public static final Charset DEFAULT_CHARSET = UTF_8;

    public enum BodyPrintType {
        HIDE, ABBREVIATE, FULL
    }

    public static RequestMessage request(String message) {
        return new StringToRequestMessage().apply(message);
    }

    public static ResponseMessage response(String message) {
        return new StringToResponseMessage().apply(message);
    }

    public static Charset charsetOf(String charset) {
        return isEmpty(charset) ? DEFAULT_CHARSET : Charset.forName(unquoted(charset));
    }

    public static String unquoted(String string) {
        return string == null ? string : string.replaceAll("'|\"", "");
    }

    public static RequestMessage merge(
            RequestMessage message,
            Map<String, Supplier<String>> parameters
    ) {
        if (parameters.isEmpty()) return message;
        MessageTemplate template = new MessageTemplate(message);
        return template.merge(parameters);
    }

    public static String toString(RequestMessage message, BodyPrintType printType) {
        StringBuilder text = new StringBuilder();
        text.append(message.getMethod())
                .append(" ")
                .append(message.getRequestTarget())
                .append(" ")
                .append(message.getProtocol());
        if (!message.getHeaders().isEmpty()) {
            text.append("\n");
        }
        text.append(
                message.getHeaders()
                        .stream()
                        .map(Header::toString)
                        .collect(joining("\n"))
        );
        Body body = message.getBody();
        if (body != null) {
            text.append("\n\n").append(toString(body, printType));
        }

        return text.toString();
    }

    public static String toString(ResponseMessage message, BodyPrintType printType) {
        StringBuilder text = new StringBuilder();
        text.append(message.getProtocol())
                .append(" ")
                .append(message.getStatus());
        if (!message.getHeaders().isEmpty()) {
            text.append("\n");
        }
        text.append(
                message.getHeaders()
                        .stream()
                        .map(Header::toString)
                        .collect(joining("\n"))
        );
        Body body = message.getBody();
        if (body != null) {
            text.append("\n\n").append(toString(body, printType));
        }

        return text.toString();
    }

    public static String toString(Body body, BodyPrintType printType) {
        return switch (printType) {
            case FULL -> body.toString();
            case ABBREVIATE -> abbreviate(body.toString(), 100);
            case HIDE -> "~~~ hidden body ~~~";
        };
    }
}
