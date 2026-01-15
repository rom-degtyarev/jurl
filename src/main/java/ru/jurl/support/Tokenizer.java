package ru.jurl.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static ru.jurl.support.Strings.isEmpty;

public class Tokenizer {
    private final List<String> items;
    private final String delimiter;
    private int currentItem = 0;

    public Tokenizer(String text, String delimiter) {
        this.items = isEmpty(text) ? emptyList() : asList(text.split(delimiter));
        this.delimiter = delimiter;
    }

    public String firstItem() {
        currentItem = 0;
        return nextItem();
    }

    public String nextItem() {
        if (currentItem >= items.size()) return null;
        return items.get(currentItem++);
    }

    public String getRest(String delimiter) {
        if (currentItem < items.size()) {
            return String.join(delimiter, items.subList(currentItem, items.size()));
        }
        return null;
    }

    public Tokenizer filter(Predicate<String> predicate) {
        String text = items
                .stream()
                .filter(predicate)
                .collect(joining(delimiter));
        return new Tokenizer(text, delimiter);
    }

    public Stream<String> stream() {
        return toList().stream();
    }

    public List<String> toList() {
        String entry;
        List<String> list = new ArrayList<>();
        while ((entry = nextItem()) != null) {
            list.add(entry);
        }
        return list;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new TreeMap<>(CASE_INSENSITIVE_ORDER);
        toList().forEach(entry -> {
            String[] strings = entry.split("=");
            map.put(strings[0].trim(), strings[1].trim());
        });
        return map;
    }

    @Override
    public String toString() {
        return join(delimiter, items);
    }
}
