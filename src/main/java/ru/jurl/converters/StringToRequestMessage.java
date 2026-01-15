package ru.jurl.converters;

import ru.jurl.http.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class StringToRequestMessage implements Function<String, RequestMessage> {
    @Override
    public RequestMessage apply(String stringMessage) {
        final RequestMessage.RequestMessageBuilder result = RequestMessage.create();
        final List<Header> headers = new ArrayList<>();
        final StringToHttpMessageParser parser = new StringToHttpMessageParser(new HttpMessageConsumer() {
            @Override
            public void onStartLine(List<String> startLineItems) {
                result.withMethod(Method.valueOf(startLineItems.getFirst()));
                result.withRequestTarget(startLineItems.get(1));
                if (startLineItems.size() > 2) {
                    result.withProtocol(ProtocolVersion.of(startLineItems.get(2)));
                }
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
