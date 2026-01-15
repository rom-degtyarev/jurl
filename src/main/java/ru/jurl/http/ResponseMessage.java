package ru.jurl.http;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

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
}
