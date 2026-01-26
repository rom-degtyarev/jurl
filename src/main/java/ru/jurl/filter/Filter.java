package ru.jurl.filter;

public interface Filter<Input, Output> {
    Output intercept(Invocation<Input, Output> invocation);
}
