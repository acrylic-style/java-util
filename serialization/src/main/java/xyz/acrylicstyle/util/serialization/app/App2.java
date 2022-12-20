package xyz.acrylicstyle.util.serialization.app;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.codec.Codec;
import xyz.acrylicstyle.util.serialization.codec.MapCodec;
import xyz.acrylicstyle.util.serialization.constructors.CodecConstructor2;

public final class App2<A, P1, P2> {
    private final MapCodec<P1, A> p1;
    private final MapCodec<P2, A> p2;

    public App2(@NotNull MapCodec<P1, A> p1, @NotNull MapCodec<P2, A> p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public @NotNull Codec<A> build(@NotNull CodecConstructor2<P1, P2, A> constructor) {
        return new GenericCodec2<>(p1, p2, constructor);
    }
}
