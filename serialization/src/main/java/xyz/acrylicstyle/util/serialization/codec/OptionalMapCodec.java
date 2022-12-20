package xyz.acrylicstyle.util.serialization.codec;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.serialization.decoder.ValueDecoder;
import xyz.acrylicstyle.util.serialization.encoder.ValueEncoder;

import java.util.Optional;
import java.util.function.Function;

public final class OptionalMapCodec<A, T> extends MapCodec<A, T> {
    private final Function<T, Optional<A>> getter;

    public OptionalMapCodec(@NotNull Codec<A> codec, @NotNull String name, @NotNull Function<T, Optional<A>> getter) {
        super(codec, name, t -> null); // super#getter is not used
        this.getter = getter;
    }

    public @Nullable A decodeValue(@NotNull ValueDecoder decoder) {
        if (decoder.decodeBoolean()) {
            return codec.decode(decoder);
        } else {
            return null;
        }
    }

    @Override
    public void encode(@NotNull T value, @NotNull ValueEncoder encoder) {
        Optional<A> opt = getter.apply(value);
        encoder.encodeBoolean(opt.isPresent());
        opt.ifPresent(a -> codec.encode(a, encoder));
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "OptionalMapCodec(" + name + ": " + codec + ")";
    }
}
