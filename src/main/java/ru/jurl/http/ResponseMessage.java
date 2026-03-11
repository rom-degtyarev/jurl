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

    public ResponseMessage.ResponseMessageBuilder copy() {
        ResponseMessage.ResponseMessageBuilder copy = create()
                .withProtocol(protocol)
                .withStatus(status)
                .withHeaders(headers);
        return body == null ? copy : copy.withBody(new Body(body.contentType(), body.content()));
    }

    @Override
    public String toString() {
        return Messages.toString(this, HEADERS);
    }
}
