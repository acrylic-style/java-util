package xyz.acrylicstyle.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class Memoize<T> {
    private final List<Map.Entry<Object[], T>> cache = Collections.synchronizedList(new ArrayList<>());

    public void put(T value, Object @NotNull ... keys) {
        cache.add(new AbstractMap.SimpleImmutableEntry<>(keys, value));
    }

    public @Nullable T get(Object @NotNull ... keys) {
        for (Map.Entry<Object[], T> entry : cache) {
            if (entry.getKey().length != keys.length) continue;
            boolean found = true;
            for (int i = 0; i < keys.length; i++) {
                if (!Objects.deepEquals(keys[i], entry.getKey()[i])) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return entry.getValue();
            }
        }
        return null;
    }

    public T computeIfAbsent(@NotNull Supplier<T> supplier, Object @NotNull ... keys) {
        T value = get(keys);
        if (value == null) {
            value = supplier.get();
            put(value, keys);
        }
        return value;
    }

    @UnmodifiableView
    public @NotNull List<Map.Entry<Object[], T>> getCache() {
        return Collections.unmodifiableList(cache);
    }
}
