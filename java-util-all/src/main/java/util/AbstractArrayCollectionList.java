package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractArrayCollectionList<C extends AbstractArrayCollectionList<C, E>, E> extends ArrayList<E> implements ICollectionList<C, E>, Cloneable, DeepCloneable {
    public AbstractArrayCollectionList() {
        super();
    }

    public AbstractArrayCollectionList(List<? extends E> list) {
        super();
        this.addAll(list);
    }

    @SafeVarargs
    public AbstractArrayCollectionList(E... v) {
        super();
        this.addAll(Arrays.asList(v));
    }

    public AbstractArrayCollectionList(java.util.Collection<? extends E> list) {
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
    public <T> AbstractArrayCollectionList<?, T> map(@NotNull Function<E, T> function) {
        AbstractArrayCollectionList<?, T> newList = createList();
        this.forEach(v -> newList.add(function.apply(v)));
        return newList;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "_ -> new", pure = true)
    public <T> AbstractArrayCollectionList<?, T> map(@NotNull BiFunction<E, Integer, T> function) {
        AbstractArrayCollectionList<?, T> newList = createList();
        final int[] index = {0};
        this.forEach(v -> {
            newList.add(function.apply(v, index[0]));
            index[0]++;
        });
        return newList;
    }

    @Override
    @NotNull
    public abstract <NT> AbstractArrayCollectionList<?, NT> createList();

    @SuppressWarnings({ "unchecked", "MethodDoesntCallSuperMethod" })
    @Override
    @NotNull
    public C clone() { return (C) superClone(); }

    @Override
    @NotNull
    public C subList(int fromIndex, int toIndex) { return ICollectionList.super.subList(fromIndex, toIndex); }

    @NotNull
    public Object superClone() { return super.clone(); }
}
