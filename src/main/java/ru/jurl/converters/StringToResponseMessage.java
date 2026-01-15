package ru.jurl.converters;

import ru.jurl.http.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class StringToResponseMessage implements Function<String, ResponseMessage> {
    @Override
    public ResponseMessage apply(String stringMessage) {
        final ResponseMessage.ResponseMessageBuilder result = ResponseMessage.create();
        final List<Header> headers = new ArrayList<>();
        final StringToHttpMessageParser parser = new StringToHttpMessageParser(new HttpMessageConsumer() {
            @Override
            public void onStartLine(List<String> startLine) {
                result.withProtocol(ProtocolVersion.of(startLine.getFirst()));
                Status status;
                if (startLine.size() > 2) {
                    status = new Status(
                            Integer.parseInt(startLine.get(1)),
                            startLine.get(2)
                    );
                } else {
                    status = Status.of(Integer.parseInt(startLine.get(1)));
                }
                result.withStatus(status);
            }

            @Override
            public void onHeader(Header header) {
                headers.add(header);
            }

            @Override
            public void onBodyContent(Body body) {
                result.withBody(body);
            }
        }, false);
        parser.parse(stringMessage);
        result.withHeaders(headers);

        return result.please();
    }
}
