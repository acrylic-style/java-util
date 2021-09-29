package util.reflect;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.ActionableResult;

public class WrappedObject<T> {
    protected final T value;

    @Contract(pure = true)
    public WrappedObject(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    @NotNull
    public ActionableResult<T> getAsResult() {
        return ActionableResult.of(value);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <R> WrappedObject<R> call(@NotNull String method, Object... args) {
        try {
            return new WrappedObject<>((R) ReflectionHelper.invokeMethodRecursively(value.getClass(), value, method, args));
        } catch (ReflectiveOperationException | ClassCastException e) {
            throw new ReflectException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <R> WrappedObject<R> field(@NotNull String field) {
        try {
            return new WrappedObject<>((R) ReflectionHelper.getFieldRecursively(value.getClass(), value, field));
        } catch (ReflectiveOperationException | ClassCastException e) {
            throw new ReflectException(e);
        }
    }
}
