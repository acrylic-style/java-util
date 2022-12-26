package xyz.acrylicstyle.util.memoize;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

final class Memoize2<T> implements Memoize<T> {
    private final Map<Object, Map<Object, T>> map = new ConcurrentHashMap<>();

    @Override
    public T get(Object @NotNull ... keys) {
        Map<Object, T> map = this.map.get(keys[0]);
        if (map == null) return null;
        return map.get(keys[1]);
    }

    @Override
    public void put(T value, Object @NotNull ... keys) {
        Map<Object, T> map = this.map.computeIfAbsent(keys[0], k -> new ConcurrentHashMap<>());
        map.put(keys[1], value);
    }

    @Override
    public T computeIfAbsent(@NotNull Supplier<T> supplier, Object @NotNull ... keys) {
        Map<Object, T> map = this.map.computeIfAbsent(keys[0], k -> new ConcurrentHashMap<>());
        return map.computeIfAbsent(keys[1], k -> supplier.get());
    }
}
