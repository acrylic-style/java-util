package xyz.acrylicstyle.util.serialization.decoder;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class MapValueDecoder implements ValueDecoder {
    private final Stack<Map<String, Object>> map = new Stack<>();
    private final Stack<String> label = new Stack<>();

    public MapValueDecoder(@NotNull Map<String, Object> map) {
        this.map.push(map);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void push(@NotNull String label) {
        this.label.push(label);
        this.map.push((Map<String, Object>) this.map.peek().get(label));
    }

    @Override
    public void pop() {
        this.label.pop();
        this.map.pop();
    }

    @Override
    public @NotNull String decodeString() {
        return (String) Objects.requireNonNull(this.map.peek().get(this.label.peek()), "value for label " + this.label.peek() + " is null");
    }

    @Override
    public int decodeInt() {
        return (int) Objects.requireNonNull(this.map.peek().get(this.label.peek()), "value for label " + this.label.peek() + " is null");
    }

    @Override
    public long decodeLong() {
        return (long) Objects.requireNonNull(this.map.peek().get(this.label.peek()), "value for label " + this.label.peek() + " is null");
    }

    @Override
    public float decodeFloat() {
        return (float) Objects.requireNonNull(this.map.peek().get(this.label.peek()), "value for label " + this.label.peek() + " is null");
    }

    @Override
    public double decodeDouble() {
        return (double) Objects.requireNonNull(this.map.peek().get(this.label.peek()), "value for label " + this.label.peek() + " is null");
    }

    @Override
    public boolean decodeBoolean() {
        return (boolean) Objects.requireNonNull(this.map.peek().get(this.label.peek()), "value for label " + this.label.peek() + " is null");
    }

    @Override
    public byte decodeByte() {
        return (byte) Objects.requireNonNull(this.map.peek().get(this.label.peek()), "value for label " + this.label.peek() + " is null");
    }

    @Override
    public char decodeChar() {
        return (char) Objects.requireNonNull(this.map.peek().get(this.label.peek()), "value for label " + this.label.peek() + " is null");
    }

    @Override
    public short decodeShort() {
        return (short) Objects.requireNonNull(this.map.peek().get(this.label.peek()), "value for label " + this.label.peek() + " is null");
    }

    @Override
    public void decodeNull() {
    }
}
