package util.base;

import net.blueberrymc.native_util.NativeUtil;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Ints {
    @Contract(pure = true)
    public static int[] toArray(Iterable<? extends Number> iterable) {
        if (iterable instanceof List) {
            try {
                return (int[]) NativeUtil.invokeObject(Class.forName("util.collection.ICollectionList").getMethod("toIntArray", List.class), null, iterable);
            } catch (ReflectiveOperationException ex) {
                throw new AssertionError(ex);
            }
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
