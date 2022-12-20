package xyz.acrylicstyle.util.serialization.app;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.codec.Codec;
import xyz.acrylicstyle.util.serialization.codec.MapCodec;
import xyz.acrylicstyle.util.serialization.constructors.CodecConstructor10;
import xyz.acrylicstyle.util.serialization.decoder.ValueDecoder;
import xyz.acrylicstyle.util.serialization.encoder.ValueEncoder;

final class GenericCodec10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, T> extends Codec<T> {
    private final MapCodec<P1, T> p1;
    private final MapCodec<P2, T> p2;
    private final MapCodec<P3, T> p3;
    private final MapCodec<P4, T> p4;
    private final MapCodec<P5, T> p5;
    private final MapCodec<P6, T> p6;
    private final MapCodec<P7, T> p7;
    private final MapCodec<P8, T> p8;
    private final MapCodec<P9, T> p9;
    private final MapCodec<P10, T> p10;
    private final CodecConstructor10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, T> constructor;

    public GenericCodec10(
            MapCodec<P1, T> p1,
            MapCodec<P2, T> p2,
            MapCodec<P3, T> p3,
            MapCodec<P4, T> p4,
            MapCodec<P5, T> p5,
            MapCodec<P6, T> p6,
            MapCodec<P7, T> p7,
            MapCodec<P8, T> p8,
            MapCodec<P9, T> p9,
            MapCodec<P10, T> p10,
            CodecConstructor10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, T> constructor
    ) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.p5 = p5;
        this.p6 = p6;
        this.p7 = p7;
        this.p8 = p8;
        this.p9 = p9;
        this.p10 = p10;
        this.constructor = constructor;
    }

    @Override
    public T decode(@NotNull ValueDecoder decoder) {
        P1 p1 = decoder.pushPop(this.p1::decodeValue).getValue();
        P2 p2 = decoder.pushPop(this.p2::decodeValue).getValue();
        P3 p3 = decoder.pushPop(this.p3::decodeValue).getValue();
        P4 p4 = decoder.pushPop(this.p4::decodeValue).getValue();
        P5 p5 = decoder.pushPop(this.p5::decodeValue).getValue();
        P6 p6 = decoder.pushPop(this.p6::decodeValue).getValue();
        P7 p7 = decoder.pushPop(this.p7::decodeValue).getValue();
        P8 p8 = decoder.pushPop(this.p8::decodeValue).getValue();
        P9 p9 = decoder.pushPop(this.p9::decodeValue).getValue();
        P10 p10 = decoder.pushPop(this.p10::decodeValue).getValue();
        return constructor.create(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
    }

    @Override
    public void encode(@NotNull T value, @NotNull ValueEncoder encoder) {
        encoder.pushPop(p1.getName(), () -> p1.encode(value, encoder));
        encoder.pushPop(p2.getName(), () -> p2.encode(value, encoder));
        encoder.pushPop(p3.getName(), () -> p3.encode(value, encoder));
        encoder.pushPop(p4.getName(), () -> p4.encode(value, encoder));
        encoder.pushPop(p5.getName(), () -> p5.encode(value, encoder));
        encoder.pushPop(p6.getName(), () -> p6.encode(value, encoder));
        encoder.pushPop(p7.getName(), () -> p7.encode(value, encoder));
        encoder.pushPop(p8.getName(), () -> p8.encode(value, encoder));
        encoder.pushPop(p9.getName(), () -> p9.encode(value, encoder));
        encoder.pushPop(p10.getName(), () -> p10.encode(value, encoder));
    }
}
