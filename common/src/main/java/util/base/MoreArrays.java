package util.base;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MoreArrays {
    @SafeVarargs
    @NotNull
    public static <E> List<E> asList(@NotNull E... list) {
        return java.util.Arrays.asList(list);
    }

    @SafeVarargs
    @NotNull
    public static <E extends Comparable<E>> List<E> asComparableList(@NotNull E... list) {
        return java.util.Arrays.asList(list);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <E> List<List<E>> toList(@NotNull Object[][] array) {
        List<List<E>> list = new ArrayList<>();
        for (Object[] obj : array) {
            List<E> innerList = new ArrayList<>();
            for (Object o : obj) {
                innerList.add((E) o);
            }
            list.add(innerList);
        }
        return list;
    }
}
