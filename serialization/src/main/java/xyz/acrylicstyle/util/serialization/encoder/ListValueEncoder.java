package xyz.acrylicstyle.util.serialization.encoder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class ListValueEncoder implements ValueEncoder {
    private final Collection<Object> list;

    public ListValueEncoder(@NotNull Collection<Object> list) {
        this.list = list;
    }

    public ListValueEncoder() {
        this.list = new ArrayList<>();
    }

    public @NotNull Collection<Object> getList() {
        return list;
    }

    @Override
    public void encodeString(@NotNull String s) {
        list.add(s);
    }

    @Override
    public void encodeInt(int i) {
        list.add(i);
    }

    @Override
    public void encodeLong(long l) {
        list.add(l);
    }

    @Override
    public void encodeFloat(float f) {
        list.add(f);
    }

    @Override
    public void encodeDouble(double d) {
        list.add(d);
    }

    @Override
    public void encodeBoolean(boolean b) {
        list.add(b);
    }

    @Override
    public void encodeByte(byte b) {
        list.add(b);
    }

    @Override
    public void encodeChar(char c) {
        list.add(c);
    }

    @Override
    public void encodeShort(short s) {
        list.add(s);
    }

    @Override
    public void encodeNull() {
        list.add(null);
    }
}
