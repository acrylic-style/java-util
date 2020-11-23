package util.io;

import org.jetbrains.annotations.Contract;
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
    @Contract
    public static long copy(@NotNull InputStream from, @NotNull OutputStream to) throws IOException {
        Validate.notNull(from, "InputStream cannot be null");
        Validate.notNull(to, "OutputStream cannot be null");
        byte[] buf = new byte[8192];
        long total = 0;
        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
            total += r;
        }
        return total;
    }

    @Contract
    public static long copy(@NotNull InputStream from, @NotNull File to) throws IOException {
        return copy(from, new FileOutputStream(to));
    }
}
