package util.reflect;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.SneakyThrow;

import java.lang.reflect.*;

public class RefConstructor<T> extends RefExecutable {
    @NotNull
    private final Constructor<T> constructor;

    public RefConstructor(@NotNull Constructor<T> constructor) {
        super(constructor);
        this.constructor = constructor;
    }

    @NotNull
    public T newInstance(Object... o) {
        try {
            return this.constructor.newInstance(o);
        } catch (ReflectiveOperationException e) { return SneakyThrow.sneaky(e); }
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(@Nullable Object o) { return this.constructor.equals(o); }

    public <S> boolean equals(@NotNull RefConstructor<S> o) { return this.constructor.equals(o.constructor); }

    @Contract("!null -> this; null -> fail")
    @NotNull
    public <S> RefConstructor<T> ifEquals(@NotNull RefConstructor<S> refConstructor) {
        if (!this.constructor.equals(refConstructor.constructor)) throw new IllegalStateException("Constructor isn't equals another constructor!");
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public <S> RefConstructor<T> ifEquals(@NotNull Constructor<S> constructor) {
        if (!this.constructor.equals(constructor)) throw new IllegalStateException("Constructor isn't equals another constructor!");
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public RefConstructor<T> accessible(boolean flag) {
        this.constructor.setAccessible(flag);
        return this;
    }

    @NotNull
    public Constructor<T> getConstructor() { return constructor; }

    @Override
    @NotNull
    public String toString() { return this.constructor.toString(); }
}
