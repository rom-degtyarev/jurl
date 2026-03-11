package ru.jurl.support.templates;

import ru.jurl.http.Parameter;
import ru.jurl.http.ResponseMessage;

import java.util.Map;

import static ru.jurl.support.templates.Templates.body;

public record ResponseTemplate(
        ResponseMessage message
) implements Template<ResponseMessage> {
    @Override
    public ResponseMessage merge(Map<String, Parameter> parameters) {
        ResponseMessage.ResponseMessageBuilder copy = message.copy();
        if (message.getBody() != null) {
            copy.withBody(body(message.getBody()).merge(parameters));
        }
        return copy.please();
    }
}
