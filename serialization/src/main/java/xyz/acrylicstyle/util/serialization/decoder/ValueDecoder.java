package xyz.acrylicstyle.util.serialization.decoder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

public interface ValueDecoder {
    /**
     * @return the label and retrieved value
     */
    default <R> Map.@NotNull Entry<String, R> pushPop(@NotNull Function<ValueDecoder, R> function) {
        String label = push();
        R value;
        try {
            value = function.apply(this);
        } finally {
            pop();
        }
        return new AbstractMap.SimpleImmutableEntry<>(label, value);
    }

    /**
     * @return the label
     */
    default @Nullable String push() {
        return null;
    }

    default void pop() {}

    @NotNull String decodeString();
    int decodeInt();
    long decodeLong();
    float decodeFloat();
    double decodeDouble();
    boolean decodeBoolean();
    byte decodeByte();
    char decodeChar();
    short decodeShort();
    void decodeNull();
}
