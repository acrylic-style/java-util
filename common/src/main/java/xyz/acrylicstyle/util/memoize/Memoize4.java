package xyz.acrylicstyle.util.memoize;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

final class Memoize4<T> implements Memoize<T> {
    private final Map<Object, Map<Object, Map<Object, Map<Object, T>>>> map = new ConcurrentHashMap<>();

    @Override
    public T get(Object @NotNull ... keys) {
        Map<Object, Map<Object, Map<Object, T>>> map = this.map.get(keys[0]);
        if (map == null) return null;
        Map<Object, Map<Object, T>> map2 = map.get(keys[1]);
        if (map2 == null) return null;
        Map<Object, T> map3 = map2.get(keys[2]);
        if (map3 == null) return null;
        return map3.get(keys[3]);
    }

    private Map<Object, T> getMap(Object @NotNull [] keys) {
        Map<Object, Map<Object, Map<Object, T>>> map = this.map.computeIfAbsent(keys[0], k -> new ConcurrentHashMap<>());
        Map<Object, Map<Object, T>> map2 = map.computeIfAbsent(keys[1], k -> new ConcurrentHashMap<>());
        return map2.computeIfAbsent(keys[2], k -> new ConcurrentHashMap<>());
    }

    @Override
    public void put(T value, Object @NotNull ... keys) {
        getMap(keys).put(keys[3], value);
    }

    @Override
    public T computeIfAbsent(@NotNull Supplier<T> supplier, Object @NotNull ... keys) {
        return getMap(keys).computeIfAbsent(keys[3], k -> supplier.get());
    }
}
