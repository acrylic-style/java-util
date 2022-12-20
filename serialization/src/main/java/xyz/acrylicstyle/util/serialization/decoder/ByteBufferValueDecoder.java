package xyz.acrylicstyle.util.serialization.decoder;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ByteBufferValueDecoder implements ValueDecoder {
    private final ByteBuffer buf;

    public ByteBufferValueDecoder(@NotNull ByteBuffer buf) {
        this.buf = Objects.requireNonNull(buf, "buf");
    }

    @Override
    public @NotNull String decodeString() {
        int length = buf.getInt();
        if (length == 0) {
            return "";
        }
        byte[] bytes = new byte[length];
        buf.get(bytes);
        return new String(bytes, StandardCharsets.UTF_16);
    }

    @Override
    public int decodeInt() {
        return buf.getInt();
    }

    @Override
    public long decodeLong() {
        return buf.getLong();
    }

    @Override
    public float decodeFloat() {
        return buf.getFloat();
    }

    @Override
    public double decodeDouble() {
        return buf.getDouble();
    }

    @Override
    public boolean decodeBoolean() {
        return buf.get() == 0;
    }

    @Override
    public byte decodeByte() {
        return buf.get();
    }

    @Override
    public char decodeChar() {
        return buf.getChar();
    }

    @Override
    public short decodeShort() {
        return buf.getShort();
    }

    @Override
    public void decodeNull() {
        byte read;
        if ((read = buf.get()) != 0) {
            throw new IllegalStateException("Unexpected value: " + Integer.toHexString(read));
        }
    }
}
