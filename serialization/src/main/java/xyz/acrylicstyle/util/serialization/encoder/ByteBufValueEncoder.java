package xyz.acrylicstyle.util.serialization.encoder;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ByteBufValueEncoder implements ValueEncoder {
    private final ByteBuf buf;

    public ByteBufValueEncoder(@NotNull ByteBuf buf) {
        this.buf = Objects.requireNonNull(buf, "buf");
    }

    @Override
    public void encodeString(@NotNull String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_16);
        buf.writeInt(bytes.length);
        if (bytes.length > 0) {
            buf.writeBytes(bytes);
        }
    }

    @Override
    public void encodeInt(int i) {
        buf.writeInt(i);
    }

    @Override
    public void encodeLong(long l) {
        buf.writeLong(l);
    }

    @Override
    public void encodeFloat(float f) {
        buf.writeFloat(f);
    }

    @Override
    public void encodeDouble(double d) {
        buf.writeDouble(d);
    }

    @Override
    public void encodeBoolean(boolean b) {
        buf.writeBoolean(b);
    }

    @Override
    public void encodeByte(byte b) {
        buf.writeByte(b);
    }

    @Override
    public void encodeChar(char c) {
        buf.writeChar(c);
    }

    @Override
    public void encodeShort(short s) {
        buf.writeShort(s);
    }

    @Override
    public void encodeNull() {
        buf.writeByte(0);
    }
}
