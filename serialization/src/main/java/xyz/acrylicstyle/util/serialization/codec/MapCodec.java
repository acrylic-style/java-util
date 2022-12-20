package xyz.acrylicstyle.util.serialization.codec;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.decoder.ValueDecoder;
import xyz.acrylicstyle.util.serialization.encoder.ValueEncoder;

import java.util.Objects;
import java.util.function.Function;

public class MapCodec<A, T> extends Codec<T> {
    protected final Codec<A> codec;
    protected final String name;
    private final Function<T, A> getter;

    public MapCodec(@NotNull Codec<A> codec, @NotNull String name, @NotNull Function<T, A> getter) {
        this.codec = Objects.requireNonNull(codec, "codec");
        this.name = Objects.requireNonNull(name, "name");
        this.getter = Objects.requireNonNull(getter, "getter");
    }

    public final @NotNull Codec<A> getTypeCodec() {
        return codec;
    }

    public final @NotNull String getName() {
        return name;
    }

    /**
     * Decodes a value from decoder. Developer should use this method instead of {@link Codec#decode(ValueDecoder)}.
     * The returned value may be null depending on the configuration.
     * @param decoder the decoder
     * @return the value
     */
    public /*@Nullable*/ A decodeValue(@NotNull ValueDecoder decoder) {
        return codec.decode(decoder);
    }

    @Override
    public void encode(@NotNull T value, @NotNull ValueEncoder encoder) {
        A a = getter.apply(value);
        if (a == null) {
            throw new IllegalArgumentException("Unexpected null value for " + name);
        }
        codec.encode(a, encoder);
    }

    @Contract(value = "_ -> fail", pure = true)
    @Override
    public final T decode(@NotNull ValueDecoder decoder) {
        throw new UnsupportedOperationException("Cannot decode MapCodec, did you mean to use getTypeCodec().decode(ValueDecoder)?");
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "MapCodec(" + name + ": " + codec + ")";
    }
}
