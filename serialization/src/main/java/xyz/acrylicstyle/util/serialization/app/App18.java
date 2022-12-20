package xyz.acrylicstyle.util.serialization.app;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.codec.Codec;
import xyz.acrylicstyle.util.serialization.codec.MapCodec;
import xyz.acrylicstyle.util.serialization.constructors.CodecConstructor18;

public final class App18<A, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18> {
    private final MapCodec<P1, A> p1;
    private final MapCodec<P2, A> p2;
    private final MapCodec<P3, A> p3;
    private final MapCodec<P4, A> p4;
    private final MapCodec<P5, A> p5;
    private final MapCodec<P6, A> p6;
    private final MapCodec<P7, A> p7;
    private final MapCodec<P8, A> p8;
    private final MapCodec<P9, A> p9;
    private final MapCodec<P10, A> p10;
    private final MapCodec<P11, A> p11;
    private final MapCodec<P12, A> p12;
    private final MapCodec<P13, A> p13;
    private final MapCodec<P14, A> p14;
    private final MapCodec<P15, A> p15;
    private final MapCodec<P16, A> p16;
    private final MapCodec<P17, A> p17;
    private final MapCodec<P18, A> p18;

    public App18(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7,
            @NotNull MapCodec<P8, A> p8,
            @NotNull MapCodec<P9, A> p9,
            @NotNull MapCodec<P10, A> p10,
            @NotNull MapCodec<P11, A> p11,
            @NotNull MapCodec<P12, A> p12,
            @NotNull MapCodec<P13, A> p13,
            @NotNull MapCodec<P14, A> p14,
            @NotNull MapCodec<P15, A> p15,
            @NotNull MapCodec<P16, A> p16,
            @NotNull MapCodec<P17, A> p17,
            @NotNull MapCodec<P18, A> p18
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
        this.p11 = p11;
        this.p12 = p12;
        this.p13 = p13;
        this.p14 = p14;
        this.p15 = p15;
        this.p16 = p16;
        this.p17 = p17;
        this.p18 = p18;
    }

    public @NotNull Codec<A> build(@NotNull CodecConstructor18<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, A> constructor) {
        return new GenericCodec18<>(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, constructor);
    }
}
