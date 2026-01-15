package ru.jurl.support;

import java.util.HexFormat;

public class Strings {
    public static Tokenizer lineTokenizer(String text) {
        return new Tokenizer(text, "\n");
    }

    public static Tokenizer wordTokenizer(String text) {
        return new Tokenizer(text, " ");
    }

    public static Tokenizer emptyLineTokenizer(String text) {
        return new Tokenizer(text, "\n\\s*\n");
    }

    public static boolean isEmpty(String string) {
        return string == null;
    }

    public static String toHexString(String string) {
        return toHexString(string.getBytes());
    }

    public static String toHexString(byte[] bytes) {
        return HexFormat.of().formatHex(bytes);
    }

    public static byte[] fromHexString(String hexString) {
        return HexFormat.of().parseHex(hexString);
    }
}
