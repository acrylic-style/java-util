package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractArrayCollectionList<E> extends ArrayList<E> implements ICollectionList<E>, Cloneable, DeepCloneable {
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
    @Contract("-> this")
    public List<E> toList() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Range(from = 0, to = 1)
    @Override
    public double distribution(@NotNull E v) {
        return filter(v2 -> v2.equals(v)).size() / (double) size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map.Entry<Double, Integer> distributionEntry(@NotNull E v) {
        int size = filter(v2 -> v2.equals(v)).size();
        return new AbstractMap.SimpleImmutableEntry<>(size / (double) size(), size);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Contract("_ -> this")
    @Override
    public AbstractArrayCollectionList<E> addChain(@NotNull E v) {
        this.add(v);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public E first() { return this.length() == 0 ? null : this.valuesArray()[0]; }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    @Contract(value = "-> new", pure = true)
    public E[] valuesArray() { return (E[]) this.toArray(); }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public E last() { return this.length() == 0 ? null : this.valuesArray()[this.length()-1]; }

    /**
     * {@inheritDoc}
     */
    @Override
    public int length() { return this.size(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public void foreach(@NotNull BiConsumer<E, Integer> action) {
        final int[] index = {0};
        this.forEach(v -> {
            action.accept(v, index[0]);
            index[0]++;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void foreach(@NotNull BiBiConsumer<E, Integer, ICollectionList<E>> action) {
        final int[] index = {0};
        this.forEach(v -> {
            action.accept(v, index[0], this.clone());
            index[0]++;
        });
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
    @Contract(value = "-> new", pure = true)
    public AbstractArrayCollectionList<E> reverse() {
        AbstractArrayCollectionList<E> target = this.clone();
        Collections.reverse(target);
        return target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <ListLike extends List<? extends E>> void putAll(@NotNull ListLike list) { this.addAll(list); }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    public AbstractArrayCollectionList<E> shuffle() {
        AbstractArrayCollectionList<E> target = this.clone();
        Collections.shuffle(target);
        return target;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract("_ -> this")
    public AbstractArrayCollectionList<E> addAll(@Nullable ICollectionList<E> list) {
        if (list == null) return this;
        list.forEach(this::add);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract("_ -> this")
    public AbstractArrayCollectionList<E> putAll(@Nullable ICollectionList<E> list) {
        return this.addAll(list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public abstract AbstractArrayCollectionList<E> newList();

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public abstract AbstractArrayCollectionList<E> newList(java.util.Collection<? extends E> list);

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "_ -> new", pure = true)
    public AbstractArrayCollectionList<E> filter(@NotNull Function<E, Boolean> filter) {
        AbstractArrayCollectionList<E> newList = newList();
        this.foreach((v, i) -> {
            if (filter.apply(v)) newList.add(v);
        });
        return newList;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public AbstractArrayCollectionList<E> filterNullable(@NotNull Function<E, Boolean> filter) {
        AbstractArrayCollectionList<E> newList = newList();
        this.foreach((v, i) -> {
            if (filter.apply(v)) newList.add(v);
        });
        return newList.size() == 0 ? null : newList;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract("_ -> this")
    public AbstractArrayCollectionList<E> removeThenReturnCollection(@NotNull E v) {
        this.remove(v);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "_ -> new", pure = true)
    public <T> AbstractArrayCollectionList<T> map(@NotNull Function<E, T> function) {
        AbstractArrayCollectionList<T> newList = (AbstractArrayCollectionList<T>) createList();
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
        AbstractArrayCollectionList<T> newList = (AbstractArrayCollectionList<T>) createList();
        final int[] index = {0};
        this.forEach(v -> {
            newList.add(function.apply(v, index[0]));
            index[0]++;
        });
        return newList;
    }

    @Override
    @NotNull
    @Contract("-> new")
    public abstract AbstractArrayCollectionList<E> clone();

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public AbstractArrayCollectionList<E> deepClone() {
        AbstractArrayCollectionList<E> list = newList();
        this.clone().forEach(v -> list.add((E) DeepCloneable.clone(v)));
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "_ -> new", pure = true)
    public String join(String s) {
        if (this.isEmpty()) return "";
        StringBuilder str = new StringBuilder();
        this.foreach((a, i) -> {
            if (i != 0) str.append(s == null ? "," : s);
            str.append(a);
        });
        return str.toString();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    public String join() {
        return this.join(null);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "_ -> new", pure = true)
    public AbstractArrayCollectionList<E> joinObject(@NotNull E v) {
        if (this.isEmpty()) return this.clone();
        AbstractArrayCollectionList<E> list = this.newList();
        this.foreach((a, i) -> {
            if (i != 0) list.add(v);
            list.add(a);
        });
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public E shift() {
        if (this.isEmpty()) return null;
        return this.remove(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public AbstractArrayCollectionList<E> shiftChain() {
        shift();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public int unshift(@Nullable E... v) {
        if (v == null || v.length == 0) return this.size();
        for (int i = 0; i < v.length; i++) this.add(i, v[i]);
        return this.size();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "_ -> new", pure = true)
    public <A, B> Collection<A, B> toMap(@NotNull Function<E, Map.Entry<A, B>> function) {
        Collection<A, B> collection = new Collection<>();
        this.forEach(v -> {
            Map.Entry<A, B> entry = function.apply(v);
            collection.add(entry.getKey(), entry.getValue());
        });
        return collection;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public <A, B> Collection<A, B> toMap(@NotNull Function<E, A> function1, @NotNull Function<E, B> function2) {
        Collection<A, B> collection = new Collection<>();
        this.forEach(v -> collection.add(function1.apply(v), function2.apply(v)));
        return collection;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public AbstractArrayCollectionList<E> concat(ICollectionList<E>... lists) {
        if (lists == null) return this.clone();
        AbstractArrayCollectionList<E> list = newList();
        list.addAll(this);
        for (ICollectionList<E> vs : lists) list.addAll(vs);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    @Contract(value = "-> new", pure = true)
    public AbstractArrayCollectionList<E> unique() { return newList(new HashSet<>(this.clone())); }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    @Contract(value = "-> new", pure = true)
    public AbstractArrayCollectionList<E> nonNull() { return newList(this.clone().filter(Objects::nonNull)); }

    @Override
    public boolean addAll(int index, java.util.Collection<? extends E> c) {
        c.forEach(v -> add(index, v));
        return true;
    }

    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {
        c.forEach(this::add);
        return true;
    }

    @NotNull
    public Object superClone() { return super.clone(); }
}
