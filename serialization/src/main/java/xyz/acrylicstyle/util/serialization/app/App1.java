package xyz.acrylicstyle.util.serialization.app;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.codec.Codec;
import xyz.acrylicstyle.util.serialization.codec.MapCodec;
import xyz.acrylicstyle.util.serialization.constructors.CodecConstructor1;

public final class App1<A, P1> {
    private final MapCodec<P1, A> p1;

    public App1(@NotNull MapCodec<P1, A> p1) {
        this.p1 = p1;
    }

    public @NotNull Codec<A> build(@NotNull CodecConstructor1<P1, A> constructor) {
        return new GenericCodec1<>(p1, constructor);
    }
}
