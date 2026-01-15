package ru.jurl.support;

import ru.jurl.converters.StringToRequestMessage;
import ru.jurl.converters.StringToResponseMessage;
import ru.jurl.http.RequestMessage;
import ru.jurl.http.ResponseMessage;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static ru.jurl.support.Strings.isEmpty;

public class Messages {
    public static final Charset DEFAULT_CHARSET = UTF_8;

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
}
