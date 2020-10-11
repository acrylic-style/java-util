package util.function;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface StringConverter<T> {
    T convert(@NotNull String s);

    static StringConverter<String> identify() { return BuiltinStringConverter.STRING; }
}
