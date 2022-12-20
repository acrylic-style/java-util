package xyz.acrylicstyle.util.serialization.codec;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public final class IntermediateMapCodec<A> {
    private final Codec<A> codec;
    private final String name;

    IntermediateMapCodec(@NotNull Codec<A> codec, @NotNull String name) {
        this.codec = Objects.requireNonNull(codec, "codec");
        this.name = Objects.requireNonNull(name, "name");
    }

    public @NotNull Codec<A> getCodec() {
        return codec;
    }

    public @NotNull String getName() {
        return name;
    }

    public <T> @NotNull MapCodec<A, T> getter(@NotNull Function<T, A> getter) {
        return new MapCodec<>(codec, name, getter);
    }
}
