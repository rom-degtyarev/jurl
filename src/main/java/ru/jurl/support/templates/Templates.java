package ru.jurl.support.templates;

import ru.jurl.http.Body;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.jurl.support.Resources.isResource;

public interface Templates {
    String RESOURCE_PLACEHOLDER_PREFIX = "<";

    static Template<Body> body(Body body) {
        return new BodyTemplate(body);
    }

    static Template<String> string(String text) {
        return new StringTemplate(text);
    }

    static boolean hasPlaceholder(String content) {
        Matcher placeholder = Pattern.compile("\\$\\{.*}").matcher(content);
        return hasResourcePlaceholder(content) || placeholder.find();
    }

    static boolean hasResourcePlaceholder(String string) {
        return string.startsWith(RESOURCE_PLACEHOLDER_PREFIX) && isResource(string.substring(1).trim());
    }
}
