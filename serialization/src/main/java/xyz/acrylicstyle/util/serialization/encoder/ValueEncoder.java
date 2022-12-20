package xyz.acrylicstyle.util.serialization.encoder;

import org.jetbrains.annotations.NotNull;

public interface ValueEncoder {
    default void pushPop(@NotNull String label, @NotNull Runnable runnable) {
        push(label);
        try {
            runnable.run();
        } finally {
            pop();
        }
    }

    default void push(@NotNull String label) {}

    default void pop() {}

    void encodeString(@NotNull String s);
    void encodeInt(int i);
    void encodeLong(long l);
    void encodeFloat(float f);
    void encodeDouble(double d);
    void encodeBoolean(boolean b);
    void encodeByte(byte b);
    void encodeChar(char c);
    void encodeShort(short s);
    void encodeNull();
}
