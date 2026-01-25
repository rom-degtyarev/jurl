package ru.jurl;

import ru.jurl.builders.ConversationBuilder;

import java.util.function.Consumer;

public class JUrl {
    public static Conversation jurl(Consumer<ConversationBuilder> init) {
        ConversationBuilder conversation = new ConversationBuilder();
        init.accept(conversation);
        return conversation.please();
    }

    public static Conversation jurl(String requestMessage) {
        return jurl(conversation -> {})
                .andThen(requestMessage);
    }
}
