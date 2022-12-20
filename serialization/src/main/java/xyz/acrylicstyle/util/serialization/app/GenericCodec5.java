package xyz.acrylicstyle.util.serialization.app;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.codec.Codec;
import xyz.acrylicstyle.util.serialization.codec.MapCodec;
import xyz.acrylicstyle.util.serialization.constructors.CodecConstructor5;
import xyz.acrylicstyle.util.serialization.decoder.ValueDecoder;
import xyz.acrylicstyle.util.serialization.encoder.ValueEncoder;

final class GenericCodec5<P1, P2, P3, P4, P5, T> extends Codec<T> {
    private final MapCodec<P1, T> p1;
    private final MapCodec<P2, T> p2;
    private final MapCodec<P3, T> p3;
    private final MapCodec<P4, T> p4;
    private final MapCodec<P5, T> p5;
    private final CodecConstructor5<P1, P2, P3, P4, P5, T> constructor;

    public GenericCodec5(
            MapCodec<P1, T> p1,
            MapCodec<P2, T> p2,
            MapCodec<P3, T> p3,
            MapCodec<P4, T> p4,
            MapCodec<P5, T> p5,
            CodecConstructor5<P1, P2, P3, P4, P5, T> constructor
    ) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.p5 = p5;
        this.constructor = constructor;
    }

    @Override
    public T decode(@NotNull ValueDecoder decoder) {
        P1 p1 = decoder.pushPop(this.p1.getName(), this.p1::decodeValue);
        P2 p2 = decoder.pushPop(this.p2.getName(), this.p2::decodeValue);
        P3 p3 = decoder.pushPop(this.p3.getName(), this.p3::decodeValue);
        P4 p4 = decoder.pushPop(this.p4.getName(), this.p4::decodeValue);
        P5 p5 = decoder.pushPop(this.p5.getName(), this.p5::decodeValue);
        return constructor.create(p1, p2, p3, p4, p5);
    }

    @Override
    public void encode(@NotNull T value, @NotNull ValueEncoder encoder) {
        encoder.pushPop(p1.getName(), () -> p1.encode(value, encoder));
        encoder.pushPop(p2.getName(), () -> p2.encode(value, encoder));
        encoder.pushPop(p3.getName(), () -> p3.encode(value, encoder));
        encoder.pushPop(p4.getName(), () -> p4.encode(value, encoder));
        encoder.pushPop(p5.getName(), () -> p5.encode(value, encoder));
    }
}