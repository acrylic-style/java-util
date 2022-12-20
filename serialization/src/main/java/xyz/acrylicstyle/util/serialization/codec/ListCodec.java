package xyz.acrylicstyle.util.serialization.codec;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.decoder.ValueDecoder;
import xyz.acrylicstyle.util.serialization.encoder.ValueEncoder;

import java.util.ArrayList;
import java.util.List;

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
        int size = decoder.pushPop("size", ValueDecoder::decodeInt);
        List<A> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(decoder.pushPop("e" + i, codec::decode));
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
