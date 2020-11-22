package util.function;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ThrowableStringConverter<T> extends StringConverter<T> {
    T doConvert(@NotNull String s) throws Throwable;

    @Override
    default T convert(@NotNull String s) {
        try {
            return doConvert(s);
        } catch (Throwable throwable) {
            return null;
        }
    }
}
