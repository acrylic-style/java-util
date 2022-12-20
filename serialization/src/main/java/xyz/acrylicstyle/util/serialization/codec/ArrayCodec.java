package xyz.acrylicstyle.util.serialization.codec;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.decoder.ValueDecoder;
import xyz.acrylicstyle.util.serialization.encoder.ValueEncoder;

public class ArrayCodec<A> extends Codec<A[]> {
    private final Codec<A> codec;

    public ArrayCodec(Codec<A> elementCodec) {
        this.codec = elementCodec;
    }

    public @NotNull Codec<A> getElementCodec() {
        return codec;
    }

    @SuppressWarnings("unchecked")
    @Override
    public A @NotNull [] decode(@NotNull ValueDecoder decoder) {
        int size = decoder.pushPop("size", ValueDecoder::decodeInt);
        A[] list = (A[]) new Object[size];
        for (int i = 0; i < size; i++) {
            list[i] = decoder.pushPop("e" + i, codec::decode);
        }
        return list;
    }

    @Override
    public void encode(A @NotNull [] value, @NotNull ValueEncoder encoder) {
        encoder.pushPop("size", () -> encoder.encodeInt(value.length));
        for (int i = 0; i < value.length; i++) {
            final int idx = i;
            encoder.pushPop("e" + idx, () -> codec.encode(value[idx], encoder));
        }
    }

    @Override
    public String toString() {
        return "ArrayCodec(" + codec + ")";
    }
}
