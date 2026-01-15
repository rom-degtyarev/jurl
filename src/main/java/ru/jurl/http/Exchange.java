package ru.jurl.http;

import java.util.function.Function;

public interface Exchange extends Function<RequestMessage, ResponseMessage> {
}
