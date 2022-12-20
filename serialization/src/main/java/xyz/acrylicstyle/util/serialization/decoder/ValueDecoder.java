package xyz.acrylicstyle.util.serialization.decoder;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface ValueDecoder {
    /**
     * @return retrieved value
     */
    default <R> R pushPop(@NotNull String label, @NotNull Function<ValueDecoder, R> function) {
        push(label);
        R value;
        try {
            value = function.apply(this);
        } finally {
            pop();
        }
        return value;
    }

    default void push(@NotNull String label) {}

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
