package ru.jurl.http;

import lombok.Builder;
import lombok.Getter;
import ru.jurl.support.Messages;

import java.util.List;

import static ru.jurl.support.Messages.BodyPrintType.HIDE;

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

    @Override
    public String toString() {
        return Messages.toString(this, HIDE);
    }
}
