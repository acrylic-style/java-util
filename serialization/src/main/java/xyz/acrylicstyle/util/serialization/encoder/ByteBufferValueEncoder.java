package xyz.acrylicstyle.util.serialization.encoder;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ByteBufferValueEncoder implements ValueEncoder {
    private final ByteBuffer buf;

    public ByteBufferValueEncoder(@NotNull ByteBuffer buf) {
        this.buf = Objects.requireNonNull(buf, "buf");
    }

    @Override
    public void encodeString(@NotNull String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_16);
        buf.putInt(bytes.length);
        if (bytes.length > 0) {
            buf.put(bytes);
        }
    }

    @Override
    public void encodeInt(int i) {
        buf.putInt(i);
    }

    @Override
    public void encodeLong(long l) {
        buf.putLong(l);
    }

    @Override
    public void encodeFloat(float f) {
        buf.putFloat(f);
    }

    @Override
    public void encodeDouble(double d) {
        buf.putDouble(d);
    }

    @Override
    public void encodeBoolean(boolean b) {
        buf.put((byte) (b ? 0 : 1));
    }

    @Override
    public void encodeByte(byte b) {
        buf.put(b);
    }

    @Override
    public void encodeChar(char c) {
        buf.putChar(c);
    }

    @Override
    public void encodeShort(short s) {
        buf.putShort(s);
    }

    @Override
    public void encodeNull() {
        buf.put((byte) 0);
    }
}
