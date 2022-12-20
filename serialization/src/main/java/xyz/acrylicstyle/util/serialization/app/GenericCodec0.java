package xyz.acrylicstyle.util.serialization.app;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.codec.Codec;
import xyz.acrylicstyle.util.serialization.constructors.CodecConstructor0;
import xyz.acrylicstyle.util.serialization.decoder.ValueDecoder;
import xyz.acrylicstyle.util.serialization.encoder.ValueEncoder;

final class GenericCodec0<T> extends Codec<T> {
    private final CodecConstructor0<T> constructor;

    public GenericCodec0(CodecConstructor0<T> constructor) {
        this.constructor = constructor;
    }

    @Override
    public T decode(@NotNull ValueDecoder decoder) {
        return constructor.create();
    }

    @Override
    public void encode(@NotNull T value, @NotNull ValueEncoder encoder) {
    }
}
