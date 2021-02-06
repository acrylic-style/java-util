package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractArrayCollectionList<E> extends ArrayList<E> implements ICollectionList<E>, Cloneable, DeepCloneable {
    public AbstractArrayCollectionList() {
        super();
    }

    public AbstractArrayCollectionList(@NotNull List<? extends E> list) {
        super();
        this.addAll(list);
    }

    @SafeVarargs
    public AbstractArrayCollectionList(@NotNull E... v) {
        super();
        this.addAll(Arrays.asList(v));
    }

    public AbstractArrayCollectionList(@NotNull java.util.Collection<? extends E> list) {
        super();
        this.addAll(list);
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

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "_ -> new", pure = true)
    public <T> AbstractArrayCollectionList<T> map(@NotNull Function<E, T> function) {
        AbstractArrayCollectionList<T> newList = createList();
        this.forEach(v -> newList.add(function.apply(v)));
        return newList;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "_ -> new", pure = true)
    public <T> AbstractArrayCollectionList<T> map(@NotNull BiFunction<E, Integer, T> function) {
        AbstractArrayCollectionList<T> newList = createList();
        final int[] index = {0};
        this.forEach(v -> {
            newList.add(function.apply(v, index[0]));
            index[0]++;
        });
        return newList;
    }

    @Override
    @NotNull
    public abstract <NT> AbstractArrayCollectionList<NT> createList();

    @SuppressWarnings({ "unchecked", "MethodDoesntCallSuperMethod" })
    @Override
    @NotNull
    public AbstractArrayCollectionList<E> clone() { return (AbstractArrayCollectionList<E>) superClone(); }

    @Override
    @NotNull
    public AbstractArrayCollectionList<E> subList(int fromIndex, int toIndex) {
        return (AbstractArrayCollectionList<E>) ICollectionList.super.subList(fromIndex, toIndex);
    }

    @NotNull
    public Object superClone() { return super.clone(); }
}
