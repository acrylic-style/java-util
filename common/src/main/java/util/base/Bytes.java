package util.base;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.Validate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A utility class to perform I/O operations such as copying InputStream to OutputStream.
 */
public class Bytes {
    public static @NotNull List<Byte> asList(@NotNull Number @NotNull ... bytes) {
        List<Byte> byteList = new ArrayList<>();
        for (Number b : bytes) {
            byteList.add(b.byteValue());
        }
        return byteList;
    }

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
        if (iterable instanceof List) {
            return toByteArray((List<? extends Number>) iterable);
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

    /**
     * Converts the list of number into primitive byte array.
     * @param list the list to convert
     * @return byte array
     */
    @Contract(pure = true)
    public static byte@NotNull[] toByteArray(@NotNull List<? extends Number> list) {
        byte[] bytes = new byte[list.size()];
        AtomicInteger i = new AtomicInteger();
        list.forEach(number -> bytes[i.getAndIncrement()] = number.byteValue());
        return bytes;
    }

    /**
     * Read all bytes from the input stream.
     * @param in input stream
     * @return byte array
     */
    @Contract(pure = true)
    public static byte@NotNull[] readFully(@NotNull InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 16];
        int read;
        while ((read = in.read(buffer)) > 0) out.write(buffer, 0, read);
        return out.toByteArray();
    }
}
