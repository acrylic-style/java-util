package xyz.acrylicstyle.util.serialization.app;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.codec.Codec;
import xyz.acrylicstyle.util.serialization.codec.MapCodec;
import xyz.acrylicstyle.util.serialization.constructors.CodecConstructor7;

public final class App7<A, P1, P2, P3, P4, P5, P6, P7> {
    private final MapCodec<P1, A> p1;
    private final MapCodec<P2, A> p2;
    private final MapCodec<P3, A> p3;
    private final MapCodec<P4, A> p4;
    private final MapCodec<P5, A> p5;
    private final MapCodec<P6, A> p6;
    private final MapCodec<P7, A> p7;

    public App7(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4,
            @NotNull MapCodec<P5, A> p5,
            @NotNull MapCodec<P6, A> p6,
            @NotNull MapCodec<P7, A> p7
    ) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.p5 = p5;
        this.p6 = p6;
        this.p7 = p7;
    }

    public @NotNull Codec<A> build(@NotNull CodecConstructor7<P1, P2, P3, P4, P5, P6, P7, A> constructor) {
        return new GenericCodec7<>(p1, p2, p3, p4, p5, p6, p7, constructor);
    }
}
