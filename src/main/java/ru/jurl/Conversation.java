package ru.jurl;

import ru.jurl.filter.Filter;
import ru.jurl.filter.InterceptedFunction;
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
    private final Function<RequestMessage, ResponseMessage> exchange;
    private final Map<String, Parameter> parameters;
    private final Map<String, Function<ResponseMessage, String>> replyParameters;
    private final Deque<RequestMessage> messages;

    public Conversation(
            Exchange exchange,
            Map<String, Parameter> parameters,
            Map<String, Function<ResponseMessage, String>> replyParameters,
            List<Filter<RequestMessage, ResponseMessage>> filters
    ) {
        this.parameters = new ConcurrentHashMap<>(parameters == null ? emptyMap() : parameters);
        this.replyParameters = new ConcurrentHashMap<>(replyParameters == null ? emptyMap() : replyParameters);
        this.messages = new ConcurrentLinkedDeque<>();
        if (exchange == null) {
            exchange = new HttpClientExchange();
        }
        if (filters == null || filters.isEmpty()) {
            this.exchange = exchange;
        } else {
            this.exchange = new InterceptedFunction<>(exchange, filters);
        }
    }

    public Conversation replyParameter(String name, Function<ResponseMessage, String> valueFun) {
        replyParameters.put(name, valueFun);
        return this;
    }

    public Conversation parameter(String name, Supplier<String> value) {
        parameters.put(name, new Parameter(name, value));
        return this;
    }

    public Conversation parameter(String name, String value) {
        parameters.put(name, new Parameter(name, () -> value));
        return this;
    }

    public Conversation andThen(RequestMessage message) {
        messages.add(message);
        return this;
    }

    public Conversation andThen(String message) {
        return andThen(request(message));
    }

    public <T> T fetch(Extractor<T> extractor) {
        List<ResponseMessage> replies = new ArrayList<>();
        RequestMessage message;
        while ((message = messages.poll()) != null) {
            RequestMessage request = merge(message, parameters);
            ResponseMessage reply = exchange.apply(request);
            replies.add(reply);
            replyParameters
                    .forEach((key, mapFunction) -> {
                        String value = mapFunction.apply(reply);
                        parameters.put(key, new Parameter(key, () -> value));
                    });
        }
        return extractor.extract(replies);
    }

    public ResponseMessage fetch() {
        return fetch(Last(response -> response));
    }
}
