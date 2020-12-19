package util.base;

import org.jetbrains.annotations.Contract;
import util.CollectionList;
import util.ICollectionList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Ints {
    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    public static int[] toArray(Iterable<? extends Number> iterable) {
        if (iterable instanceof CollectionList) {
            return ((CollectionList<?>) iterable).toIntArray();
        } else if (iterable instanceof List) {
            return ICollectionList.toIntArray((List<? extends Number>) iterable);
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
}
