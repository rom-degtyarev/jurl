package ru.jurl.support.templates;

import ru.jurl.http.Parameter;
import ru.jurl.http.RequestMessage;

import java.util.Map;

import static ru.jurl.support.templates.Templates.*;

public record RequestTemplate(RequestMessage message) implements Template<RequestMessage> {
    @Override
    public RequestMessage merge(Map<String, Parameter> parameters) {
        RequestMessage.RequestMessageBuilder copy = message.copy();
        copy.withRequestTarget(string(message.getRequestTarget()).merge(parameters));
        if (message.getBody() != null) {
            copy.withBody(body(message.getBody()).merge(parameters));
        }
        return copy.please();
    }
}
