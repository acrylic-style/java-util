package util.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Maps {
    @Nullable
    public static <K, V> Map.Entry<K, V> findEntry(@NotNull Map<K, V> map, @NotNull Predicate<? super K> predicate) {
        Validate.notNull(predicate, "predicate cannot be null");
        return filterKeys(map, predicate::test).entrySet().stream().findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <K, V> Map<K, V> filterKeys(@NotNull Map<K, V> map, @NotNull  Function<K, Boolean> filter) {
        Map<K, V> newList = new HashMap<>();
        Object[] values = map.values().toArray(new Object[0]);
        foreachKeys(map, (k, i) -> {
            if (filter.apply(k)) newList.put(k, (V) values[i]);
        });
        return newList;
    }

    public static <K> void foreachKeys(@NotNull Map<K, ?> map, @NotNull BiConsumer<K, Integer> action) {
        final int[] index = {0};
        map.keySet().forEach(k -> {
            action.accept(k, index[0]);
            index[0]++;
        });
    }

    @Nullable
    public static <K, V> V find(@NotNull Map<K, V> map, @NotNull K key) {
        Validate.notNull(key, "key cannot be null");
        Map.Entry<K, V> entry = findEntry(map, key::equals);
        if (entry != null) return entry.getValue();
        return null;
    }
}
