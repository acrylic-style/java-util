package util.function;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ThrowableConsumer<T> {
    void accept(T t) throws Throwable;

    @NotNull
    default <R> ThrowableConsumerFunction<T, R> toFunction() {
        return t -> {
            accept(t);
            return null;
        };
    }

    interface ThrowableConsumerFunction<T, R> extends ThrowableFunction<T, R> {
        R run(T t) throws Throwable;
    }
}
