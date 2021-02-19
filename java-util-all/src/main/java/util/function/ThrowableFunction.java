package util.function;

import org.jetbrains.annotations.NotNull;
import util.Chain;
import util.ThrowableActionableResult;

import java.util.function.Function;

public interface ThrowableFunction<T, R> extends Function<T, ThrowableActionableResult<R>>, Chain<ThrowableFunction<T, R>> {
    @Override
    @NotNull
    default ThrowableActionableResult<R> apply(T t) {
        try {
            return ThrowableActionableResult.success(run(t));
        } catch (Throwable throwable) {
            return ThrowableActionableResult.failure(throwable);
        }
    }

    R run(T t) throws Throwable;
}