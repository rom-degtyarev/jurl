package ru.jurl.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.function.Function;

@RequiredArgsConstructor
public class Invocation<Input, Output> implements Function<Input, Output> {
    private final Function<Input, Output> target;
    private final Iterator<Filter<Input, Output>> filters;
    @Getter
    private Input input;
    @Getter
    private Output output;

    public Output invoke() {
        return apply(getInput());
    }

    @Override
    public Output apply(Input input) {
        this.input = input;
        if (filters.hasNext()) {
            Filter<Input, Output> filter = filters.next();
            return filter.intercept(this);
        }
        this.output = target.apply(input);
        return this.output;
    }
}
