package util.collection;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import util.function.BiBiConsumer;
import util.DeepCloneable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings({ "NullableProblems", "unchecked" })
public class CollectionSet<V> extends HashSet<V> implements ICollectionList<V>, Cloneable, DeepCloneable {
    public CollectionSet() {
        super();
    }

    public CollectionSet(List<? extends V> list) {
        super();
        this.addAll(list);
    }

    @SafeVarargs
    public CollectionSet(V... v) {
        super();
        this.addAll(Arrays.asList(v));
    }

    public CollectionSet(java.util.Collection<? extends V> list) {
        super();
        this.addAll(list);
    }

    @NotNull
    @Override
    public Iterator<V> iterator() {
        return super.iterator();
    }

    @NotNull
    @Override
    public Spliterator<V> spliterator() {
        return super.spliterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Range(from = 0, to = 1) double distribution(@NotNull V v) {
        return filter(v2 -> v2.equals(v)).size() / (double) size();
    }

    /**
     * {@inheritDoc}
     * the value (count) is always 1 or 0 since this list is {@link Set}.
     */
    @Override
    public Map.Entry<Double, Integer> distributionEntry(@NotNull V v) {
        int size = filter(v2 -> v2.equals(v)).size();
        return new AbstractMap.SimpleImmutableEntry<>(size / (double) size(), size);
    }

    /**
     * {@inheritDoc}
     * @deprecated order isn't guaranteed, may return different values per call.
     */
    @Nullable
    @Override
    @Deprecated
    public V first() { return this.unique().length() == 0 ? null : this.unique().valuesArray()[0]; }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    @Contract(value = "-> new", pure = true)
    public V[] valuesArray() { return (V[]) this.unique().toArray0(); }

    private Object[] toArray0() { return super.toArray(); }

    /**
     * {@inheritDoc}
     * @deprecated order isn't guaranteed, may return different values per call.
     */
    @Nullable
    @Override
    @Deprecated
    public V last() { return this.unique().length() == 0 ? null : this.unique().valuesArray()[this.unique().length()-1]; }

    /**
     * {@inheritDoc}
     */
    @Override
    public int length() {
        return this.unique().size();
    }

    /**
     * {@inheritDoc}
     * Remember: order isn't guaranteed.
     */
    @Override
    public void foreach(@NotNull BiConsumer<V, Integer> action) {
        final int[] index = {0};
        this.unique().forEach(v -> {
            action.accept(v, index[0]);
            index[0]++;
        });
    }

    /**
     * {@inheritDoc}
     * Remember: order isn't guaranteed.
     */
    @Override
    public void foreach(@NotNull BiBiConsumer<V, Integer, ICollectionList<V>> action) {
        final int[] index = {0};
        this.unique().forEach(v -> {
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
    public V put(@NotNull V value) {
        super.add(value);
        return value;
    }

    /**
     * {@inheritDoc}
     * @deprecated order isn't guaranteed, so this call does nothing.
     */
    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    @Deprecated
    public CollectionSet<V> reverse() {
        return this; // set does not have order
    }

    /**
     * {@inheritDoc}
     * @deprecated order isn't guaranteed, so this call does nothing.
     */
    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    @Deprecated
    public CollectionSet<V> shuffle() {
        return this; // set does not have order
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <ListLike extends List<? extends V>> void putAll(@NotNull ListLike list) {
        if (list instanceof ICollectionList) {
            addAll((ICollectionList<V>) list);
        } else {
            this.addAll(list);
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract("_ -> this")
    public CollectionSet<V> addAll(@Nullable ICollectionList<V> list) {
        return (CollectionSet<V>) ICollectionList.super.addAll(list).unique();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract("_ -> this")
    public CollectionSet<V> putAll(@Nullable ICollectionList<V> list) {
        return this.addAll(list);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "_ -> new", pure = true)
    public CollectionSet<V> filter(@NotNull Function<V, Boolean> filter) {
        CollectionSet<V> newList = newList();
        this.foreach((v, i) -> {
            if (filter.apply(v)) newList.add(v);
        });
        return newList.unique();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public CollectionSet<V> filterNullable(@NotNull Function<V, Boolean> filter) {
        CollectionSet<V> newList = newList();
        this.foreach((v, i) -> {
            if (filter.apply(v)) newList.add(v);
        });
        return newList.size() == 0 ? null : newList.unique();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract("-> new")
    @SuppressWarnings("unchecked")
    public CollectionSet<V> clone() {
        return (CollectionSet<V>) super.clone();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "_ -> new", pure = true)
    public <T> CollectionSet<T> map(@NotNull Function<V, T> function) {
        CollectionSet<T> newList = new CollectionSet<>();
        this.forEach(v -> newList.add(function.apply(v)));
        return newList.unique();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "_ -> new", pure = true)
    public <T> CollectionSet<T> map(@NotNull BiFunction<V, Integer, T> function) {
        CollectionSet<T> newList = new CollectionSet<>();
        final int[] index = {0};
        this.forEach(v -> {
            newList.add(function.apply(v, index[0]));
            index[0]++;
        });
        return newList.unique();
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
        this.unique().foreach((a, i) -> {
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
    @Contract(value = "_ -> new", pure = true)
    public CollectionSet<V> joinObject(@NotNull V v) {
        return (CollectionSet<V>) ICollectionList.super.joinObject(v).unique();
    }

    /**
     * {@inheritDoc}
     * Subclasses of this class must override this method.
     */
    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    public CollectionSet<V> newList() {
        return new CollectionSet<>();
    }

    /**
     * {@inheritDoc}
     * Subclasses of this class must override this method.
     */
    @Override
    public @NotNull CollectionSet<V> newList(java.util.@Nullable Collection<? extends V> list) {
        return new CollectionSet<>(list);
    }

    @Override
    public @NotNull <E> CollectionSet<E> createList() { return new CollectionSet<>(); }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    public String join() {
        return this.unique().join(null);
    }

    /**
     * {@inheritDoc}
     * @deprecated order isn't guaranteed, so index parameter does nothing. use {@link #addAll(java.util.Collection)}
     * instead.
     */
    @Contract("_, _ -> fail")
    @Override
    @Deprecated
    public boolean addAll(int index, @NotNull java.util.Collection<? extends V> c) { return addAll(c); }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CollectionSet)) return false;
        CollectionSet<?> list = (CollectionSet<?>) o;
        return super.equals(list.unique());
    }

    @Override
    public @NotNull CollectionSet<V> unique() { return (CollectionSet<V>) ICollectionList.super.unique(); }

    /**
     * {@inheritDoc}
     * @deprecated this implementation always throws exception since it does not have order
     */
    @Contract(value = "_ -> fail")
    @Override
    @Deprecated
    public final V get(int index) {
        throw new UnsupportedOperationException("CollectionSet doesn't have order");
    }

    /**
     * {@inheritDoc}
     * @deprecated this implementation always throws exception since it does not have order
     */
    @Contract("_, _ -> fail")
    @Override
    @Deprecated
    public final V set(int index, V element) {
        throw new UnsupportedOperationException("CollectionSet doesn't have order");
    }

    /**
     * {@inheritDoc}
     * @deprecated order isn't guaranteed, so index parameter does nothing
     */
    @Contract("_, _ -> fail")
    @Override
    @Deprecated
    public void add(int index, V element) { this.add(element); }

    /**
     * {@inheritDoc}
     * @deprecated this implementation always throws exception since it does not have order
     */
    @Deprecated
    @Contract("_ -> fail")
    @Override
    public final V remove(int index) {
        throw new UnsupportedOperationException("CollectionSet doesn't have order");
    }

    /**
     * {@inheritDoc}
     * @deprecated this implementation always throws exception since it does not have order
     */
    @Deprecated
    @Contract("_ -> fail")
    @Override
    public final int indexOf(Object o) {
        throw new UnsupportedOperationException("CollectionSet doesn't have order");
    }

    /**
     * {@inheritDoc}
     * @deprecated this implementation always throws exception since it does not have order
     */
    @Deprecated
    @Contract("_ -> fail")
    @Override
    public final int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("CollectionSet doesn't have order");
    }

    /**
     * {@inheritDoc}
     * @deprecated this implementation always throws exception since it does not have order (we can't determine
     * what element/index to shift)
     */
    @Deprecated
    @Contract("-> fail")
    @Override
    @Nullable
    public final V shift() {
        throw new UnsupportedOperationException("CollectionSet doesn't have order");
    }

    /**
     * {@inheritDoc}
     * @deprecated this implementation always throws exception since it does not have order
     */
    @SafeVarargs
    @Deprecated
    @Contract("_ -> fail")
    @Override
    public final int unshift(@Nullable V... v) {
        throw new UnsupportedOperationException("CollectionSet doesn't have order");
    }

    /**
     * The <b>CollectionList.of()</b> method creates a new
     * CollectionList instance from a variable number of
     * arguments, regardless of number or type of the arguments.
     * @param t Elements of which to create the array.
     * @return A new CollectionList instance.
     */
    @NotNull
    @Contract("_ -> new")
    @SafeVarargs
    public static <T> CollectionSet<T> of(T... t) {
        return new CollectionSet<>(t).unique();
    }

    /**
     * {@inheritDoc}
     * <b>This implementation always returns the new list of CollectionList with values.</b>
     */
    @Contract(pure = true)
    @NotNull
    public CollectionList<V> toList() {
        return new CollectionList<>(this.unique());
    }

    @NotNull
    @Override
    public V[] toArray() {
        return valuesArray();
    }
}