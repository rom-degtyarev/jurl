package ru.jurl.http;

import lombok.Builder;
import lombok.Getter;
import ru.jurl.support.Messages;

import java.util.List;

import static ru.jurl.support.Bodies.valueOf;
import static ru.jurl.support.Messages.PrintOption.HEADERS;

@Builder(
        setterPrefix = "with",
        builderMethodName = "create",
        buildMethodName = "please"
)
@Getter
public class RequestMessage {
    private final Method method;
    private final String requestTarget;
    @Builder.Default
    private final ProtocolVersion protocol = ProtocolVersion.HTTP_1_1;
    private final List<Header> headers;
    private final Body body;

    public RequestMessageBuilder copy() {
        RequestMessageBuilder copy = create()
                .withMethod(method)
                .withRequestTarget(requestTarget)
                .withProtocol(protocol)
                .withHeaders(headers);
        return body == null ? copy : copy.withBody(valueOf(body.content(), body.contentType()));
    }

    @Override
    public String toString() {
        return Messages.toString(this, HEADERS);
    }
}
