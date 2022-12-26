package xyz.acrylicstyle.util.memoize;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

final class Memoize1<T> implements Memoize<T> {
    private final Map<Object, T> map = new ConcurrentHashMap<>();

    @Override
    public T get(Object @NotNull ... keys) {
        return map.get(keys[0]);
    }

    @Override
    public void put(T value, Object @NotNull ... keys) {
        map.put(keys[0], value);
    }

    @Override
    public T computeIfAbsent(@NotNull Supplier<T> supplier, Object @NotNull ... keys) {
        return map.computeIfAbsent(keys[0], k -> supplier.get());
    }
}
