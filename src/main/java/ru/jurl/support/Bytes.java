package ru.jurl.support;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Bytes {
    public static final byte NEW_LINE = '\n';
    public static final byte CR = '\r';
    public static final byte TAB = '\t';
    public static final byte SPACE = ' ';

    public static byte[] trim(byte[] array) {
        return trimBegin(trimEnd(array));
    }

    public static byte[] trimBegin(byte[] array) {
        int currentIndex = 0;
        for (int i = 0; i < array.length; i++) {
            byte b = array[i];
            if (b == NEW_LINE || b == CR || b == TAB || b == SPACE)
                currentIndex++;
            else break;
        }
        if (currentIndex > 0) {
            return Arrays.copyOfRange(array, currentIndex, array.length);
        } else {
            return array;
        }
    }

    public static byte[] trimEnd(byte[] array) {
        int currentIndex = 0;
        for (int i = array.length - 1; i >= 0; i--) {
            byte b = array[i];
            if (b == NEW_LINE || b == CR || b == TAB || b == SPACE)
                currentIndex++;
            else break;
        }
        if (currentIndex > 0) {
            return Arrays.copyOfRange(array, 0, array.length - currentIndex);
        } else {
            return array;
        }
    }

    public static List<byte[]> split(byte[] array, byte[] delimiter) {
        return split(array, delimiter, true);
    }

    public static List<byte[]> split(byte[] array, byte[] delimiter, boolean skipEmptyChunk) {
        List<byte[]> byteArrays = new LinkedList<>();
        if (delimiter.length == 0) {
            return byteArrays;
        }
        int currentIndex = 0;

        next:
        for (int i = 0; i < array.length - delimiter.length + 1; i++) {
            for (int j = 0; j < delimiter.length; j++) {
                if (array[i + j] != delimiter[j]) {
                    continue next;
                }
            }

            if (currentIndex != i)
                byteArrays.add(Arrays.copyOfRange(array, currentIndex, i));
            else if (!skipEmptyChunk) {
                byteArrays.add(new byte[0]);
            }
            currentIndex = i + delimiter.length;
        }

        if (currentIndex < array.length)
            byteArrays.add(Arrays.copyOfRange(array, currentIndex, array.length));

        return byteArrays;
    }
}
