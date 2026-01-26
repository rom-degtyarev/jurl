package ru.jurl.filter;

import java.util.function.Function;

public class InterceptedFunction<Input, Output> implements Function<Input, Output> {
    private final Function<Input, Output> target;
    private final Iterable<Filter<Input, Output>> filters;

    public InterceptedFunction(
            Function<Input, Output> target,
            Iterable<Filter<Input, Output>> filters
    ) {
        this.target = target;
        this.filters = filters;
    }

    @Override
    public Output apply(Input input) {
        Invocation<Input, Output> invocation = new Invocation<>(target, filters.iterator());
        return invocation.apply(input);
    }
}
