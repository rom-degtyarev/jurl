package ru.jurl.support.templates;

import ru.jurl.http.Body;
import ru.jurl.http.Parameter;
import ru.jurl.support.Bodies;

import java.util.Map;

import static ru.jurl.support.templates.Templates.string;

public record BodyTemplate(
        Body body
) implements Template<Body> {
    @Override
    public Body merge(Map<String, Parameter> parameters) {
        String content = string(body.toString()).merge(parameters);
        return Bodies.valueOf(content, body.contentType());
    }
}
