package xyz.acrylicstyle.util.serialization.decoder;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

public class ListValueDecoder implements ValueDecoder {
    private final Stack<Object> stack;

    public ListValueDecoder(@NotNull Stack<Object> stack, boolean reverse) {
        this.stack = stack;
        if (reverse) {
            Collections.reverse(this.stack);
        }
    }

    public ListValueDecoder(@NotNull Collection<?> collection, boolean reverse) {
        this.stack = new Stack<>();
        this.stack.addAll(collection);
        if (reverse) {
            Collections.reverse(this.stack);
        }
    }

    @Override
    public @NotNull String decodeString() {
        return (String) stack.pop();
    }

    @Override
    public int decodeInt() {
        return (Integer) stack.pop();
    }

    @Override
    public long decodeLong() {
        return (Long) stack.pop();
    }

    @Override
    public float decodeFloat() {
        return (Float) stack.pop();
    }

    @Override
    public double decodeDouble() {
        return (Double) stack.pop();
    }

    @Override
    public boolean decodeBoolean() {
        return (Boolean) stack.pop();
    }

    @Override
    public byte decodeByte() {
        return (Byte) stack.pop();
    }

    @Override
    public char decodeChar() {
        return (Character) stack.pop();
    }

    @Override
    public short decodeShort() {
        return (Short) stack.pop();
    }

    @Override
    public void decodeNull() {
        stack.pop();
    }
}
