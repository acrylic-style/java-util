package util.function;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.ActionableResult;

import java.util.function.Function;

@FunctionalInterface
public interface StringConverter<T> extends Function<String, T> {
    T convert(@NotNull String s);

    @NotNull
    default ActionableResult<T> getAsResult(@NotNull String s) {
        return ActionableResult.of(convert(s));
    }

    @Override
    default T apply(@NotNull String s) { return convert(s); }

    @Contract(pure = true)
    @NotNull
    static StringConverter<String> identity() { return BuiltinStringConverter.STRING; }
}
