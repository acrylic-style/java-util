package util.base;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Lists {
    @SafeVarargs
    @NotNull
    public static <T> List<T> concat(@NotNull Iterable<T>... iterables) {
        ArrayList<T> arrayList = new ArrayList<>();
        for (Iterable<T> ts : iterables) {
            ts.forEach(arrayList::add);
        }
        return arrayList;
    }

    @Contract("_, _ -> param1")
    public static <T> @NotNull List<T> addTo(@NotNull List<T> list, @Nullable T value) {
        list.add(value);
        return list;
    }

    public static <E> void forEachIndexed(@NotNull List<E> list, @NotNull BiConsumer<E, Integer> action) {
        final int[] index = {0};
        list.forEach(v -> {
            action.accept(v, index[0]);
            index[0]++;
        });
    }

    public static <E, T> List<T> mapIndexed(@NotNull List<E> list, @NotNull BiFunction<E, @NotNull Integer, T> action) {
        final int[] index = {0};
        return list.stream().map(e -> action.apply(e, index[0])).collect(Collectors.toList());
    }

    @SafeVarargs
    public static <V> int unshift(@NotNull List<V> list, @Nullable V... v) {
        if (v == null || v.length == 0) return list.size();
        for (int i = 0; i < v.length; i++) list.add(i, v[i]);
        return list.size();
    }

    public static @NotNull String join(@NotNull List<?> list, @Nullable String s) {
        if (list.isEmpty()) return "";
        StringBuilder str = new StringBuilder();
        forEachIndexed(list, (a, i) -> {
            if (i != 0) str.append(s == null ? "," : s);
            str.append(a);
        });
        return str.toString();
    }

    @Nullable
    public static <T> T first(@NotNull List<T> list) {
        if (list.size() == 0) return null;
        return list.get(0);
    }
}
