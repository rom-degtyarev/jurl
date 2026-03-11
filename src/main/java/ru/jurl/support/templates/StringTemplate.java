package ru.jurl.support.templates;

import ru.jurl.http.Parameter;
import ru.jurl.support.Resources;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static ru.jurl.support.templates.Templates.hasResourcePlaceholder;

public record StringTemplate(
        String text
) implements Template<String> {

    @Override
    public String merge(Map<String, Parameter> parameters) {
        var text = stream(this.text.split("\n"))
                .filter(line -> !line.trim().startsWith("#"))
                .map(line -> {
                    String trimLine = line.trim();
                    if (hasResourcePlaceholder(trimLine))
                        return Resources.of(trimLine.substring(1).trim()).get();
                    else
                        return line;
                }).collect(joining("\n"));
        for (Map.Entry<String, Parameter> entry : parameters.entrySet()) {
            String parameter = entry.getKey();
            String value = entry.getValue().get();
            text = text.replaceAll("\\$\\{" + parameter + "}", value);
        }
        return text;
    }
}

