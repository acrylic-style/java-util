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
                return Objects.requireNonNull(name, "name");
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

    // --- Builtin types

    // Primitive types
    public static final Codec<Character> CHAR = new Codec<Character>() {
        @Override
        public Character decode(@NotNull ValueDecoder decoder) {
            return decoder.decodeChar();
        }

        @Override
        public void encode(@NotNull Character value, @NotNull ValueEncoder encoder) {
            encoder.encodeChar(value);
        }

        @Override
        public String toString() {
            return "Codec(char)";
        }
    };
    public static final Codec<Integer> INT = new Codec<Integer>() {
        @Override
        public Integer decode(@NotNull ValueDecoder decoder) {
            return decoder.decodeInt();
        }

        @Override
        public void encode(@NotNull Integer value, @NotNull ValueEncoder encoder) {
            encoder.encodeInt(value);
        }

        @Override
        public String toString() {
            return "Codec(int)";
        }
    };
    public static final Codec<Long> LONG = new Codec<Long>() {
        @Override
        public Long decode(@NotNull ValueDecoder decoder) {
            return decoder.decodeLong();
        }

        @Override
        public void encode(@NotNull Long value, @NotNull ValueEncoder encoder) {
            encoder.encodeLong(value);
        }

        @Override
        public String toString() {
            return "Codec(long)";
        }
    };
    public static final Codec<Short> SHORT = new Codec<Short>() {
        @Override
        public Short decode(@NotNull ValueDecoder decoder) {
            return decoder.decodeShort();
        }

        @Override
        public void encode(@NotNull Short value, @NotNull ValueEncoder encoder) {
            encoder.encodeShort(value);
        }

        @Override
        public String toString() {
            return "Codec(short)";
        }
    };
    public static final Codec<Byte> BYTE = new Codec<Byte>() {
        @Override
        public Byte decode(@NotNull ValueDecoder decoder) {
            return decoder.decodeByte();
        }

        @Override
        public void encode(@NotNull Byte value, @NotNull ValueEncoder encoder) {
            encoder.encodeByte(value);
        }

        @Override
        public String toString() {
            return "Codec(byte)";
        }
    };
    public static final Codec<Boolean> BOOLEAN = new Codec<Boolean>() {
        @Override
        public Boolean decode(@NotNull ValueDecoder decoder) {
            return decoder.decodeBoolean();
        }

        @Override
        public void encode(@NotNull Boolean value, @NotNull ValueEncoder encoder) {
            encoder.encodeBoolean(value);
        }

        @Override
        public String toString() {
            return "Codec(boolean)";
        }
    };
    public static final Codec<Float> FLOAT = new Codec<Float>() {
        @Override
        public Float decode(@NotNull ValueDecoder decoder) {
            return decoder.decodeFloat();
        }

        @Override
        public void encode(@NotNull Float value, @NotNull ValueEncoder encoder) {
            encoder.encodeFloat(value);
        }

        @Override
        public String toString() {
            return "Codec(float)";
        }
    };
    public static final Codec<Double> DOUBLE = new Codec<Double>() {
        @Override
        public Double decode(@NotNull ValueDecoder decoder) {
            return decoder.decodeDouble();
        }

        @Override
        public void encode(@NotNull Double value, @NotNull ValueEncoder encoder) {
            encoder.encodeDouble(value);
        }

        @Override
        public String toString() {
            return "Codec(double)";
        }
    };
    public static final Codec<String> STRING = new Codec<String>() {
        @Override
        public String decode(@NotNull ValueDecoder decoder) {
            return decoder.decodeString();
        }

        @Override
        public void encode(@NotNull String value, @NotNull ValueEncoder encoder) {
            encoder.encodeString(value);
        }

        @Override
        public String toString() {
            return "Codec(String)";
        }
    };

    // Extra types
    public static final Codec<java.util.UUID> UUID =
            Codec.<UUID>builder()
                    .group(LONG.fieldOf("most").getter(java.util.UUID::getMostSignificantBits), LONG.fieldOf("least").getter(java.util.UUID::getLeastSignificantBits))
                    .build(java.util.UUID::new);
}
