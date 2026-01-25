package ru.jurl.support;

import ru.jurl.http.Body;
import ru.jurl.http.RequestMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public record MessageTemplate(RequestMessage message) {
    public RequestMessage merge(Map<String, Supplier<String>> parameters) {
        RequestMessage.RequestMessageBuilder copy = message.copy();
        Map<String, String> values = fetch(parameters);
        copy.withRequestTarget(merge(message.getRequestTarget(), values));
        if (message.getBody() != null) {
            copy.withBody(merge(message.getBody(), values));
        }
        return copy.please();
    }

    private Body merge(Body body, Map<String, String> values) {
        String string = merge(body.toString(), values);
        return new Body(body.contentType(), string.getBytes(body.contentType().getCharset()));
    }

    private String merge(String text, Map<String, String> parameters) {
        if (text.startsWith("<")) {
            text = Resources.of(text.substring(1).trim()).get();
        }
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String parameter = entry.getKey();
            String value = entry.getValue();
            text = text.replaceAll("\\$\\{" + parameter + "}", value);
        }
        return text;
    }

    private Map<String, String> fetch(Map<String, Supplier<String>> parameters) {
        Map<String, String> map = new HashMap<>();
        parameters.forEach((key, value) -> {
            map.put(key, value.get());
        });
        return map;
    }
}
