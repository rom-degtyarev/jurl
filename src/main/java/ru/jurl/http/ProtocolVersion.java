package ru.jurl.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static ru.jurl.support.Strings.isEmpty;

@AllArgsConstructor
@Getter
public enum ProtocolVersion {
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("HTTP/2");

    private final String code;

    @Override
    public String toString() {
        return code;
    }

    public static ProtocolVersion of(String value) {
        if (isEmpty(value)) return HTTP_1_1;
        for (ProtocolVersion version : values()) {
            if (version.code.equals(value)) return version;
        }
        throw new IllegalArgumentException("Protocol version not supported: " + value);
    }
}
