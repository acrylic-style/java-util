package xyz.acrylicstyle.util.serialization.codec;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.decoder.ValueDecoder;
import xyz.acrylicstyle.util.serialization.encoder.ValueEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListCodec<A> extends Codec<List<A>> {
    private final Codec<A> codec;

    public ListCodec(Codec<A> elementCodec) {
        this.codec = elementCodec;
    }

    public @NotNull Codec<A> getElementCodec() {
        return codec;
    }

    @Override
    public List<A> decode(@NotNull ValueDecoder decoder) {
        Map.Entry<String, Integer> sizeEntry = decoder.pushPop(ValueDecoder::decodeInt);
        if (!"size".equals(sizeEntry.getKey())) {
            throw new IllegalStateException("Incorrect label: " + sizeEntry.getKey() + " (expected: size)");
        }
        int size = sizeEntry.getValue();
        List<A> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Map.Entry<String, A> entry = decoder.pushPop(codec::decode);
            if (!("e" + i).equals(entry.getKey())) {
                throw new IllegalStateException("Incorrect label: " + entry.getKey() + " (expected: e" + i + ")");
            }
            list.add(entry.getValue());
        }
        return list;
    }

    @Override
    public void encode(@NotNull List<A> value, @NotNull ValueEncoder encoder) {
        encoder.pushPop("size", () -> encoder.encodeInt(value.size()));
        for (int i = 0; i < value.size(); i++) {
            final int idx = i;
            encoder.pushPop("e" + idx, () -> codec.encode(value.get(idx), encoder));
        }
    }

    @Override
    public String toString() {
        return "ListCodec(" + codec + ")";
    }
}
