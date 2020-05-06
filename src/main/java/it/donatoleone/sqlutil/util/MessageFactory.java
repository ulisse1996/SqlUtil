package it.donatoleone.sqlutil.util;

import java.util.function.Supplier;

public class MessageFactory {

    private static final String FORMAT_MESSAGE = "%s can't be null !";
    private static final String IMMUTABLE = "Value can't be modified";

    private MessageFactory() {}

    public static Supplier<String> notNull(String val) {
        return () -> String.format(FORMAT_MESSAGE, val);
    }

    public static String immutable() {
        return IMMUTABLE;
    }
}
