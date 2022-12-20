package xyz.acrylicstyle.util.serialization.app;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.codec.Codec;
import xyz.acrylicstyle.util.serialization.codec.MapCodec;
import xyz.acrylicstyle.util.serialization.constructors.CodecConstructor3;

public final class App3<A, P1, P2, P3> {
    private final MapCodec<P1, A> p1;
    private final MapCodec<P2, A> p2;
    private final MapCodec<P3, A> p3;

    public App3(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3
    ) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public @NotNull Codec<A> build(@NotNull CodecConstructor3<P1, P2, P3, A> constructor) {
        return new GenericCodec3<>(p1, p2, p3, constructor);
    }
}
