package util.function;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@FunctionalInterface
public interface StringConverter<T> extends Function<String, T> {
    T convert(@NotNull String s);

    @Override
    default T apply(@NotNull String s) { return convert(s); }

    static StringConverter<String> identify() { return BuiltinStringConverter.STRING; }
}
