package xyz.acrylicstyle.util.serialization.decoder;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ByteBufValueDecoder implements ValueDecoder {
    private final ByteBuf buf;

    public ByteBufValueDecoder(@NotNull ByteBuf buf) {
        this.buf = Objects.requireNonNull(buf, "buf");
    }

    @Override
    public @NotNull String decodeString() {
        int length = buf.readInt();
        if (length == 0) {
            return "";
        }
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_16);
    }

    @Override
    public int decodeInt() {
        return buf.readInt();
    }

    @Override
    public long decodeLong() {
        return buf.readLong();
    }

    @Override
    public float decodeFloat() {
        return buf.readFloat();
    }

    @Override
    public double decodeDouble() {
        return buf.readDouble();
    }

    @Override
    public boolean decodeBoolean() {
        return buf.readBoolean();
    }

    @Override
    public byte decodeByte() {
        return buf.readByte();
    }

    @Override
    public char decodeChar() {
        return buf.readChar();
    }

    @Override
    public short decodeShort() {
        return buf.readShort();
    }

    @Override
    public void decodeNull() {
        byte read;
        if ((read = buf.readByte()) != 0) {
            throw new IllegalStateException("Unexpected value: " + Integer.toHexString(read));
        }
    }
}
