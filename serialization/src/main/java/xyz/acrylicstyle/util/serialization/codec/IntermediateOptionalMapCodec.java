package xyz.acrylicstyle.util.serialization.codec;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class IntermediateOptionalMapCodec<A> {
    private final Codec<A> codec;
    private final String name;

    IntermediateOptionalMapCodec(@NotNull Codec<A> codec, @NotNull String name) {
        this.codec = Objects.requireNonNull(codec, "codec");
        this.name = Objects.requireNonNull(name, "name");
    }

    public @NotNull Codec<A> getCodec() {
        return codec;
    }

    public @NotNull String getName() {
        return name;
    }

    public <T> @NotNull OptionalMapCodec<A, T> getter(@NotNull Function<T, Optional<A>> getter) {
        return new OptionalMapCodec<>(codec, name, getter);
    }
}
