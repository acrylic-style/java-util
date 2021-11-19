package util.base;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Ints {
    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    public static int[] toArray(Iterable<? extends Number> iterable) {
        if (iterable instanceof List) {
            return toIntArray((List<? extends Number>) iterable);
        }
        final int[][] bytes = { new int[8] };
        AtomicInteger index = new AtomicInteger();
        iterable.forEach(number -> {
            if (index.getAndIncrement() >= bytes[0].length) {
                int[] bytes1 = new int[index.get() + 8];
                System.arraycopy(bytes[0], 0, bytes1, 0, bytes[0].length);
                bytes[0] = bytes1;
            }
            bytes[0][index.get()] = number.byteValue();
        });
        return bytes[0];
    }

    /**
     * Converts the list of number into primitive int array.
     * @param list the list to convert
     * @return int array
     */
    @Contract(pure = true)
    public static int@NotNull [] toIntArray(@NotNull List<? extends Number> list) {
        int[] bytes = new int[list.size()];
        AtomicInteger i = new AtomicInteger();
        list.forEach(number -> bytes[i.getAndIncrement()] = number.intValue());
        return bytes;
    }
}
