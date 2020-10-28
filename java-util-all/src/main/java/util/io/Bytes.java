package util.io;

import com.google.common.io.ByteStreams;
import org.jetbrains.annotations.NotNull;
import util.Validate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An utility class to perform I/O operations such as copying InputStream to OutputStream.
 */
public final class Bytes {
    @SuppressWarnings("UnusedReturnValue")
    public static long copy(@NotNull InputStream from, @NotNull OutputStream to) throws IOException {
        Validate.notNull(from, "InputStream cannot be null");
        Validate.notNull(to, "OutputStream cannot be null");
        return ByteStreams.copy(from, to);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static long copy(@NotNull InputStream from, @NotNull File to) throws IOException {
        return copy(from, new FileOutputStream(to));
    }
}
