package util.base;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.CollectionList;
import util.ICollectionList;
import util.Validate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    public static byte[] toArray(Iterable<? extends Number> iterable) {
        if (iterable instanceof CollectionList) {
            return ((CollectionList<?>) iterable).toByteArray();
        } else if (iterable instanceof List) {
            return ICollectionList.toByteArray((List<? extends Number>) iterable);
        }
        final byte[][] bytes = { new byte[8] };
        AtomicInteger index = new AtomicInteger();
        iterable.forEach(number -> {
            if (index.getAndIncrement() >= bytes[0].length) {
                byte[] bytes1 = new byte[index.get() + 8];
                System.arraycopy(bytes[0], 0, bytes1, 0, bytes[0].length);
                bytes[0] = bytes1;
            }
            bytes[0][index.get()] = number.byteValue();
        });
        return bytes[0];
    }
}
