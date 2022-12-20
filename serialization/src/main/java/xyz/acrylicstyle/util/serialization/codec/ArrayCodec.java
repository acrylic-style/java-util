package xyz.acrylicstyle.util.serialization.codec;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.decoder.ValueDecoder;
import xyz.acrylicstyle.util.serialization.encoder.ValueEncoder;

import java.util.Map;

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
        Map.Entry<String, Integer> sizeEntry = decoder.pushPop(ValueDecoder::decodeInt);
        if (!"size".equals(sizeEntry.getKey())) {
            throw new IllegalStateException("Incorrect label: " + sizeEntry.getKey() + " (expected: size)");
        }
        int size = sizeEntry.getValue();
        A[] list = (A[]) new Object[size];
        for (int i = 0; i < size; i++) {
            Map.Entry<String, A> entry = decoder.pushPop(codec::decode);
            if (!("e" + i).equals(entry.getKey())) {
                throw new IllegalStateException("Incorrect label: " + entry.getKey() + " (expected: e" + i + ")");
            }
            list[i] = entry.getValue();
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
