package ru.jurl.support;

import ru.jurl.converters.StringToRequestMessage;
import ru.jurl.converters.StringToResponseMessage;
import ru.jurl.http.*;
import ru.jurl.support.templates.RequestTemplate;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static ru.jurl.support.Messages.PrintOption.*;
import static ru.jurl.support.Strings.abbreviate;
import static ru.jurl.support.Strings.isEmpty;

public class Messages {
    public static final Charset DEFAULT_CHARSET = UTF_8;

    public enum PrintOption {
        HEADERS, HIDE_BODY, ABBREVIATE_BODY, BODY
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
            Map<String, Parameter> parameters
    ) {
        if (parameters.isEmpty()) return message;
        RequestTemplate template = new RequestTemplate(message);
        return template.merge(parameters);
    }

    public static String toString(RequestMessage message, PrintOption ...options) {
        StringBuilder text = new StringBuilder();
        text.append(message.getMethod())
                .append(" ")
                .append(message.getRequestTarget())
                .append(" ")
                .append(message.getProtocol());
        Set<PrintOption> printOptions = Set.of(options);
        if (printOptions.contains(HEADERS)) {
            if (!message.getHeaders().isEmpty()) {
                text.append("\n");
            }
            text.append(
                    message.getHeaders()
                            .stream()
                            .map(Header::toString)
                            .collect(joining("\n"))
            );
        }
        Body body = message.getBody();
        if (body != null) {
            text.append("\n\n").append(toString(body, options));
        }

        return text.toString();
    }

    public static String toString(ResponseMessage message, PrintOption ...options) {
        StringBuilder text = new StringBuilder();
        text.append(message.getProtocol())
                .append(" ")
                .append(message.getStatus());
        Set<PrintOption> printOptions = Set.of(options);
        if (printOptions.contains(HEADERS)) {
            if (!message.getHeaders().isEmpty()) {
                text.append("\n");
            }
            text.append(
                    message.getHeaders()
                            .stream()
                            .map(Header::toString)
                            .collect(joining("\n"))
            );
        }
        Body body = message.getBody();
        if (body != null) {
            text.append("\n\n").append(toString(body, options));
        }

        return text.toString();
    }

    public static String toString(Body body, PrintOption ...options) {
        if (body == null) return "";
        Set<PrintOption> printOptions = Set.of(options);
        if (printOptions.contains(HIDE_BODY)) return "~~~ hidden body ~~~";
        String bodyString = body.toString();
        if (printOptions.contains(ABBREVIATE_BODY)) return abbreviate(bodyString, 100);
        if (printOptions.contains(BODY)) return bodyString;
        if (bodyString.length() <= 250) return bodyString;
        return bodyString.substring(0, 100) + "\n~~~ abbreviated body ~~~\n" + bodyString.substring(bodyString.length() - 100);
    }
}
