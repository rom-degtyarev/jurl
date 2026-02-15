package ru.jurl.http;

import lombok.Builder;
import lombok.Getter;
import ru.jurl.support.Messages;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static ru.jurl.support.Headers.getFirst;
import static ru.jurl.support.Messages.PrintOption.HEADERS;

@Builder(
        setterPrefix = "with",
        builderMethodName = "create",
        buildMethodName = "please"
)
@Getter
public class ResponseMessage {
    private final Status status;
    private final ProtocolVersion protocol;
    private final List<Header> headers;
    private final Body body;

    public <T extends Header> T getHeader(Predicate<Header> filter) {
        Optional<Header> header = getFirst(headers, filter);
        return (T) header.orElse(null);
    }

    @Override
    public String toString() {
        return Messages.toString(this, HEADERS);
    }
}
