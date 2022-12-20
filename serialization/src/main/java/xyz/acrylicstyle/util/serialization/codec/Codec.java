package xyz.acrylicstyle.util.serialization.codec;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.app.App0;
import xyz.acrylicstyle.util.serialization.decoder.ValueDecoder;
import xyz.acrylicstyle.util.serialization.encoder.ValueEncoder;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public abstract class Codec<A> implements Encoder<A>, Decoder<A> {
    @Contract(value = "_, _, _ -> new", pure = true)
    public static <A> @NotNull Codec<A> of(@NotNull Encoder<A> paramEncoder, @NotNull Decoder<A> paramDecoder, @NotNull String name) {
        return new Codec<A>() {
            @Override
            public void encode(@NotNull A value, @NotNull ValueEncoder encoder) {
                paramEncoder.encode(value, encoder);
            }

            @Override
            public A decode(@NotNull ValueDecoder decoder) {
                return paramDecoder.decode(decoder);
            }

            @Override
            public @NotNull String toString() {
                return "Codec(" + Objects.requireNonNull(name, "name") + ")";
            }
        };
    }

    @Contract("_ -> new")
    public static <E> @NotNull Codec<List<E>> list(@NotNull Codec<E> elementCodec) {
        return new ListCodec<>(elementCodec);
    }

    @Contract("_ -> new")
    public static <E> @NotNull Codec<E[]> array(@NotNull Codec<E> elementCodec) {
        return new ArrayCodec<>(elementCodec);
    }

    @Contract(value = " -> new", pure = true)
    public static <A> @NotNull App0<A> builder() {
        return App0.getInstance();
    }

    @Contract("_ -> new")
    public final @NotNull IntermediateMapCodec<A> fieldOf(@NotNull String name) {
        return new IntermediateMapCodec<>(this, name);
    }

    @Contract("_ -> new")
    public final @NotNull IntermediateOptionalMapCodec<A> optionalFieldOf(@NotNull String name) {
        return new IntermediateOptionalMapCodec<>(this, name);
    }

    @Contract(" -> new")
    public final @NotNull Codec<List<A>> list() {
        return list(this);
    }

    @Contract(" -> new")
    public final @NotNull Codec<A[]> array() {
        return array(this);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public final <S> @NotNull Codec<S> xmap(@NotNull Function<A, S> getter, @NotNull Function<S, A> setter) {
        return of((value, encoder) -> encode(setter.apply(value), encoder), decoder -> getter.apply(decode(decoder)), this + "[xmapped]");
    }

    @Contract(value = "_ -> new", pure = true)
    public final @NotNull Codec<A> named(@NotNull String name) {
        return of(this, this, name);
    }

    // --- Builtin types

    // Primitive types
    public static final Codec<Character> CHAR = of((o, e) -> e.encodeChar(o), ValueDecoder::decodeChar, "char");
    public static final Codec<Integer> INT = of((o, e) -> e.encodeInt(o), ValueDecoder::decodeInt, "int");
    public static final Codec<Long> LONG = of((o, e) -> e.encodeLong(o), ValueDecoder::decodeLong, "long");
    public static final Codec<Short> SHORT = of((o, e) -> e.encodeShort(o), ValueDecoder::decodeShort, "short");
    public static final Codec<Byte> BYTE = of((o, e) -> e.encodeByte(o), ValueDecoder::decodeByte, "byte");
    public static final Codec<Boolean> BOOLEAN = of((o, e) -> e.encodeBoolean(o), ValueDecoder::decodeBoolean, "boolean");
    public static final Codec<Float> FLOAT = of((o, e) -> e.encodeFloat(o), ValueDecoder::decodeFloat, "float");
    public static final Codec<Double> DOUBLE = of((o, e) -> e.encodeDouble(o), ValueDecoder::decodeDouble, "double");
    public static final Codec<String> STRING = of((o, e) -> e.encodeString(o), ValueDecoder::decodeString, "String");

    // Extra types
    public static final Codec<java.util.UUID> UUID =
            Codec.<UUID>builder()
                    .group(LONG.fieldOf("most").getter(java.util.UUID::getMostSignificantBits), LONG.fieldOf("least").getter(java.util.UUID::getLeastSignificantBits))
                    .build(java.util.UUID::new)
                    .named("UUID");
}
