package util.function;

import org.jetbrains.annotations.NotNull;
import util.ThrowableActionableResult;

@FunctionalInterface
public interface ThrowableStringConverter<T> extends StringConverter<T> {
    T doConvert(@NotNull String s) throws Throwable;

    @NotNull
    @Override
    default ThrowableActionableResult<T> getAsResult(@NotNull String s) {
        try {
            return ThrowableActionableResult.success(doConvert(s));
        } catch (Throwable throwable) {
            return ThrowableActionableResult.error(throwable);
        }
    }

    @Override
    default T convert(@NotNull String s) {
        try {
            return doConvert(s);
        } catch (Throwable throwable) {
            return null;
        }
    }
}
