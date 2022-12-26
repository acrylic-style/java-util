package xyz.acrylicstyle.util.memoize;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

final class Memoize3<T> implements Memoize<T> {
    private final Map<Object, Map<Object, Map<Object, T>>> map = new ConcurrentHashMap<>();

    @Override
    public T get(Object @NotNull ... keys) {
        Map<Object, Map<Object, T>> map = this.map.get(keys[0]);
        if (map == null) return null;
        Map<Object, T> map2 = map.get(keys[1]);
        if (map2 == null) return null;
        return map2.get(keys[2]);
    }

    @Override
    public void put(T value, Object @NotNull ... keys) {
        Map<Object, Map<Object, T>> map = this.map.computeIfAbsent(keys[0], k -> new ConcurrentHashMap<>());
        Map<Object, T> map2 = map.computeIfAbsent(keys[1], k -> new ConcurrentHashMap<>());
        map2.put(keys[2], value);
    }

    @Override
    public T computeIfAbsent(@NotNull Supplier<T> supplier, Object @NotNull ... keys) {
        Map<Object, Map<Object, T>> map = this.map.computeIfAbsent(keys[0], k -> new ConcurrentHashMap<>());
        Map<Object, T> map2 = map.computeIfAbsent(keys[1], k -> new ConcurrentHashMap<>());
        return map2.computeIfAbsent(keys[2], k -> supplier.get());
    }
}
