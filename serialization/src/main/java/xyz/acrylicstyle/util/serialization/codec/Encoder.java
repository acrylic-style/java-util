package xyz.acrylicstyle.util.serialization.codec;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.encoder.ValueEncoder;

public interface Encoder<A> {
    void encode(@NotNull A value, @NotNull ValueEncoder encoder);
}
