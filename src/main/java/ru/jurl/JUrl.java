package ru.jurl;

import java.util.function.Consumer;

public class JUrl {
    public static Conversation jurl(Consumer<Conversation.ConversationBuilder> init) {
        Conversation.ConversationBuilder conversation = Conversation.create();
        init.accept(conversation);
        return conversation.please();
    }

    public static Conversation jurl(String requestMessage) {
        return jurl(conversation -> {})
                .andThen(requestMessage);
    }
}
