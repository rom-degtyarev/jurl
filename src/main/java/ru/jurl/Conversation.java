package ru.jurl;

import ru.jurl.http.*;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.emptyMap;
import static ru.jurl.support.Extractors.Last;
import static ru.jurl.support.Messages.merge;
import static ru.jurl.support.Messages.request;

public class Conversation {
    private final Exchange exchange;
    private final Map<String, Supplier<String>> parameters;
    private final Map<String, Function<ResponseMessage, String>> replyParameters;
    private final Deque<RequestMessage> messages;

    public Conversation(
            Exchange exchange,
            Map<String, Supplier<String>> parameters,
            Map<String, Function<ResponseMessage, String>> replyParameters
    ) {
        this.exchange = exchange != null ? exchange : new HttpClientExchange();
        this.parameters = new ConcurrentHashMap<>(parameters == null ? emptyMap() : parameters);
        this.replyParameters = new ConcurrentHashMap<>(replyParameters == null ? emptyMap() : replyParameters);
        this.messages = new ConcurrentLinkedDeque<>();
    }

    public Conversation replyParameter(String name, Function<ResponseMessage, String> valueFun) {
        replyParameters.put(name, valueFun);
        return this;
    }

    public Conversation parameter(String name, Supplier<String> value) {
        parameters.put(name, value);
        return this;
    }

    public Conversation parameter(String name, String value) {
        parameters.put(name, () -> value);
        return this;
    }

    public Conversation andThen(RequestMessage message) {
        messages.add(message);
        return this;
    }

    public Conversation andThen(String message) {
        return andThen(request(message));
    }

    public <T> T run(Extractor<T> extractor) {
        List<ResponseMessage> replies = new ArrayList<>();
        RequestMessage message;
        while ((message = messages.poll()) != null) {
            RequestMessage request = merge(message, parameters);
            ResponseMessage reply = exchange.apply(request);
            replies.add(reply);
            replyParameters
                    .forEach((key, mapFunction) -> {
                        String value = mapFunction.apply(reply);
                        parameters.put(key, () -> value);
                    });
        }
        return extractor.extract(replies);
    }

    public ResponseMessage run() {
        return run(Last(response -> response));
    }
}
