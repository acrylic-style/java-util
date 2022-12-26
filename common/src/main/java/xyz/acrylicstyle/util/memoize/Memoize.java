package xyz.acrylicstyle.util.memoize;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface Memoize<T> {
    @Contract("_ -> new")
    static <T> @NotNull Memoize<T> of(int size) {
        switch (size) {
            case 1: return new Memoize1<>();
            case 2: return new Memoize2<>();
            case 3: return new Memoize3<>();
            case 4: return new Memoize4<>();
            case 5: return new Memoize5<>();
            default: throw new IllegalArgumentException("size must be 1 to 5");
        }
    }

    T get(Object @NotNull ... keys);

    void put(T value, Object @NotNull ... keys);

    T computeIfAbsent(@NotNull Supplier<T> supplier, Object @NotNull ... keys);
}
