package ru.jurl.builders;

import ru.jurl.Conversation;
import ru.jurl.filter.Filter;
import ru.jurl.filter.LoggerFilter;
import ru.jurl.http.Exchange;
import ru.jurl.http.RequestMessage;
import ru.jurl.http.ResponseMessage;
import ru.jurl.support.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Map.entry;
import static ru.jurl.support.Maps.map;
import static ru.jurl.support.Maps.mapOf;
import static ru.jurl.support.Messages.BodyPrintType.HIDE;

public class ConversationBuilder {
    private Exchange exchange;
    private Map<String, Supplier<String>> parameters;
    private Map<String, Function<ResponseMessage, String>> replyParameters;
    private List<Filter<RequestMessage, ResponseMessage>> filters;

    public Conversation please() {
        return new Conversation(exchange, parameters, replyParameters, filters);
    }

    public ConversationBuilder enableLogger() {
        return enableLogger(HIDE);
    }

    public ConversationBuilder enableLogger(Messages.BodyPrintType printType) {
        if (filters == null) {
            filters = new ArrayList<>();
        }
        filters.add(new LoggerFilter(printType));
        return this;
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
