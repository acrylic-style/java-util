package xyz.acrylicstyle.util.serialization.codec;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.serialization.decoder.ValueDecoder;

public interface Decoder<A> {
    A decode(@NotNull ValueDecoder decoder);
}
