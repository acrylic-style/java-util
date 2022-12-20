package xyz.acrylicstyle.util.serialization.app;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.codec.Codec;
import xyz.acrylicstyle.util.serialization.codec.MapCodec;
import xyz.acrylicstyle.util.serialization.constructors.CodecConstructor4;

public final class App4<A, P1, P2, P3, P4> {
    private final MapCodec<P1, A> p1;
    private final MapCodec<P2, A> p2;
    private final MapCodec<P3, A> p3;
    private final MapCodec<P4, A> p4;

    public App4(
            @NotNull MapCodec<P1, A> p1,
            @NotNull MapCodec<P2, A> p2,
            @NotNull MapCodec<P3, A> p3,
            @NotNull MapCodec<P4, A> p4
    ) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
    }

    public @NotNull Codec<A> build(@NotNull CodecConstructor4<P1, P2, P3, P4, A> constructor) {
        return new GenericCodec4<>(p1, p2, p3, p4, constructor);
    }
}
