package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractCollectionList<E> implements ICollectionList<E>, Cloneable, DeepCloneable {
    public AbstractCollectionList() {
        super();
    }

    public AbstractCollectionList(List<? extends E> list) {
        super();
        this.addAll(list);
    }

    @SafeVarargs
    public AbstractCollectionList(E... v) {
        super();
        this.addAll(Arrays.asList(v));
    }

    public AbstractCollectionList(java.util.Collection<? extends E> list) {
        super();
        this.addAll(list);
    }

    @NotNull
    @Override
    @SuppressWarnings({ "unchecked", "SuspiciousSystemArraycopy", "ConstantConditions" })
    public <T> T[] toArray(@NotNull T[] a) {
        int size = size();
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(toArray(), size, a.getClass());
        System.arraycopy(toArray(), 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract("_ -> param1")
    public E put(@NotNull E value) {
        this.add(value);
        return value;
    }

    @Override
    public abstract @NotNull <E1> AbstractCollectionList<E1> createList();

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    @Contract(value = "_ -> new", pure = true)
    public <T> AbstractCollectionList<T> map(@NotNull Function<E, T> function) {
        return (AbstractCollectionList<T>) ICollectionList.super.map(function);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    @Contract(value = "_ -> new", pure = true)
    public <T> AbstractCollectionList<T> map(@NotNull BiFunction<E, Integer, T> function) {
        return (AbstractCollectionList<T>) ICollectionList.super.map(function);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    @NotNull
    @Contract("-> new")
    public AbstractCollectionList<E> clone() { return (AbstractCollectionList<E>) newList(this); }
}
