package ru.jurl.support;

import ru.jurl.http.Body;
import ru.jurl.http.Parameter;
import ru.jurl.http.RequestMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record MessageTemplate(RequestMessage message) {
    public RequestMessage merge(Map<String, Parameter> parameters) {
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
        return Bodies.valueOf(string, body.contentType());
    }

    public static boolean hasPlaceholder(String content) {
        Matcher placeholder = Pattern.compile("\\$\\{.*}").matcher(content);
        return content.startsWith("<") || placeholder.find();
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

    private Map<String, String> fetch(Map<String, Parameter> parameters) {
        Map<String, String> map = new HashMap<>();
        parameters.forEach((key, value) -> {
            map.put(key, value.get());
        });
        return map;
    }
}
