package ru.jurl.builders;

import ru.jurl.Conversation;
import ru.jurl.http.Exchange;
import ru.jurl.http.ResponseMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Map.entry;
import static ru.jurl.support.Maps.map;
import static ru.jurl.support.Maps.mapOf;

public class ConversationBuilder {
    private Exchange exchange;
    private Map<String, Supplier<String>> parameters;
    private Map<String, Function<ResponseMessage, String>> replyParameters;

    public Conversation please() {
        return new Conversation(exchange, parameters, replyParameters);
    }

    public ConversationBuilder withExchange(Exchange exchange) {
        this.exchange = exchange;
        return this;
    }

    public ConversationBuilder withParameters(Map<String, Supplier<String>> parameters) {
        parameters().putAll(parameters);
        return this;
    }

    public ConversationBuilder withParameters(String... parameters) {
        Map<String, Supplier<String>> map = map(
                mapOf(parameters),
                entry -> entry(entry.getKey(), entry::getValue)
        );
        return withParameters(map);
    }

    public ConversationBuilder withParameter(String parameter, String value) {
        parameters().put(parameter, () -> value);
        return this;
    }

    public ConversationBuilder withParameter(String parameter, Supplier<String> value) {
        parameters().put(parameter, value);
        return this;
    }

    public ConversationBuilder withReplyParameter(String key, Function<ResponseMessage, String> value) {
        replyParameters().put(key, value);
        return this;
    }

    private Map<String, Supplier<String>> parameters() {
        if (this.parameters == null) {
            this.parameters = new ConcurrentHashMap<>();
        }
        return this.parameters;
    }

    private Map<String, Function<ResponseMessage, String>> replyParameters() {
        if (this.replyParameters == null) {
            this.replyParameters = new ConcurrentHashMap<>();
        }
        return this.replyParameters;
    }
}
