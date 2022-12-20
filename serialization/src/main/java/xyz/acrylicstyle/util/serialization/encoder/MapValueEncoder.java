package xyz.acrylicstyle.util.serialization.encoder;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * An encoder which encodes values into a map. Due to the nature of this encoder, this encoder cannot encode a single
 * value alone like {@link xyz.acrylicstyle.util.serialization.codec.Codec#INT}. In other words, this encoder can only
 * encode values only if the "label" is set during encoding which can be done via {@link #push(String)} or
 * {@link #pushPop(String, Runnable)}.
 * <p>
 * <b>Note: Decoder is not implemented and the value of the map is subject to change.</b>
 */
@ApiStatus.Experimental
public class MapValueEncoder implements ValueEncoder {
    private final Stack<Map<String, Object>> stack = new Stack<>();
    private final Stack<String> label = new Stack<>();

    public MapValueEncoder(@NotNull Map<String, Object> map) {
        stack.push(map);
    }

    public MapValueEncoder() {
        this(new HashMap<>());
    }

    public @NotNull Map<String, Object> getCurrentMap() {
        return stack.peek();
    }

    @SuppressWarnings("unchecked")
    public @NotNull List<String> getCurrentLabels() {
        return (List<String>) getCurrentMap().computeIfAbsent("|labels|", k -> new ArrayList<>());
    }

    @Override
    public void push(@NotNull String label) {
        Map<String, Object> map = new HashMap<>();
        getCurrentLabels().add(label);
        getCurrentMap().put(label, map);
        this.stack.push(map);
        this.label.push(label);
    }

    @Override
    public void pop() {
        stack.pop();
        label.pop();
    }

    @Override
    public void encodeString(@NotNull String s) {
        String l = label.peek();
        getCurrentLabels().add(l);
        getCurrentMap().put(l, s);
    }

    @Override
    public void encodeInt(int i) {
        String l = label.peek();
        getCurrentLabels().add(l);
        getCurrentMap().put(l, i);
    }

    @Override
    public void encodeLong(long l) {
        String lb = label.peek();
        getCurrentLabels().add(lb);
        getCurrentMap().put(lb, l);
    }

    @Override
    public void encodeFloat(float f) {
        String l = label.peek();
        getCurrentLabels().add(l);
        getCurrentMap().put(l, f);
    }

    @Override
    public void encodeDouble(double d) {
        String l = label.peek();
        getCurrentLabels().add(l);
        getCurrentMap().put(l, d);
    }

    @Override
    public void encodeBoolean(boolean b) {
        String l = label.peek();
        getCurrentLabels().add(l);
        getCurrentMap().put(l, b);
    }

    @Override
    public void encodeByte(byte b) {
        String l = label.peek();
        getCurrentLabels().add(l);
        getCurrentMap().put(l, b);
    }

    @Override
    public void encodeChar(char c) {
        String l = label.peek();
        getCurrentLabels().add(l);
        getCurrentMap().put(l, c);
    }

    @Override
    public void encodeShort(short s) {
        String l = label.peek();
        getCurrentLabels().add(l);
        getCurrentMap().put(l, s);
    }

    @Override
    public void encodeNull() {
        String l = label.peek();
        getCurrentLabels().add(l);
        getCurrentMap().put(l, null);
    }
}
