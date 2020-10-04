package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This CollectionList provides a better feature than
 * just a List or ArrayList, like using #map without
 * using the Stream. Additionally, this list provides
 * the some useful methods. This list is based on the
 * {@link java.util.ArrayList ArrayList} and you may not
 * get the performance advantages by using this list.
 * This list just provides the useful methods.
 */
public class CollectionList<V> extends AbstractArrayCollectionList<V> implements ICollectionList<V>, Cloneable, Serializable {
    private static final long serialVersionUID = 11_25L;

    public CollectionList() {
        super();
    }

    public CollectionList(List<? extends V> list) {
        super(list);
    }

    @SafeVarargs
    public CollectionList(V... v) {
        super(v);
    }

    public CollectionList(java.util.Collection<? extends V> list) {
        super(list);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract("-> new")
    @SuppressWarnings("unchecked")
    public CollectionList<V> clone() {
        return (CollectionList<V>) superClone();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    public CollectionList<V> newList() {
        return new CollectionList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull AbstractArrayCollectionList<V> newList(Collection<? extends V> list) {
        return new CollectionList<>(list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull <E> CollectionList<E> createList() { return new CollectionList<>(); }

    /**
     * {@inheritDoc}
     */
    @SafeVarargs
    @Override
    public @NotNull
    final CollectionList<V> concat(ICollectionList<V>... lists) {
        return (CollectionList<V>) super.concat(lists);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull CollectionList<V> unique() {
        return (CollectionList<V>) super.unique();
    }

    /**
     * {@inheritDoc}
     */
    public CollectionList<V> filterNullable(@NotNull Function<V, Boolean> filter) {
        return (CollectionList<V>) super.filterNullable(filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CollectionList)) return false;
        CollectionList<?> list = (CollectionList<?>) o;
        return super.equals(list);
    }

    /**
     * The <b>CollectionList.of()</b> method creates a new
     * CollectionList instance from a variable number of
     * arguments, regardless of number or type of the arguments.
     * @param t Elements of which to create the array.
     * @return A new CollectionList instance.
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    public static <T> CollectionList<T> of(T... t) { return new CollectionList<>(t); }

    // fix return type

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull CollectionList<V> addChain(@NotNull V v) {
        return (CollectionList<V>) super.addChain(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull CollectionList<V> reverse() {
        return (CollectionList<V>) super.reverse();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull <T> CollectionList<T> map(@NotNull Function<V, T> function) {
        return (CollectionList<T>) super.map(function);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull <T> CollectionList<T> map(@NotNull BiFunction<V, Integer, T> function) {
        return (CollectionList<T>) super.map(function);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull CollectionList<V> deepClone() {
        return (CollectionList<V>) super.deepClone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull CollectionList<V> joinObject(@NotNull V v) {
        return (CollectionList<V>) super.joinObject(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull CollectionList<V> shuffle() {
        return (CollectionList<V>) super.shuffle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull CollectionList<V> addAll(@Nullable ICollectionList<V> list) {
        return (CollectionList<V>) super.addAll(list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull CollectionList<V> putAll(@Nullable ICollectionList<V> list) {
        return (CollectionList<V>) super.putAll(list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull CollectionList<V> filter(@NotNull Function<V, Boolean> filter) {
        return (CollectionList<V>) super.filter(filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull CollectionList<V> removeThenReturnCollection(@NotNull V v) {
        return (CollectionList<V>) super.removeThenReturnCollection(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull CollectionList<V> shiftChain() {
        return (CollectionList<V>) super.shiftChain();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull CollectionList<V> nonNull() {
        return (CollectionList<V>) super.nonNull();
    }

    // These methods are just overriding, to prevent NoSuchMethodError on old code

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull List<V> toList() {
        return super.toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Range(from = 0, to = 1) double distribution(@NotNull V v) {
        return super.distribution(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map.Entry<Double, Integer> distributionEntry(@NotNull V v) {
        return super.distributionEntry(v);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public V first() {
        return super.first();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public V[] valuesArray() {
        return super.valuesArray();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public V last() {
        return super.last();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int length() {
        return super.length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void foreach(@NotNull BiConsumer<V, Integer> action) {
        super.foreach(action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void foreach(@NotNull BiBiConsumer<V, Integer, ICollectionList<V>> action) {
        super.foreach(action);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public V put(@NotNull V value) {
        return super.put(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <ListLike extends List<? extends V>> void putAll(@NotNull ListLike list) {
        super.putAll(list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String join(String s) {
        return super.join(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String join() {
        return super.join();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public V shift() {
        return super.shift();
    }

    /**
     * {@inheritDoc}
     */
    @SafeVarargs
    @Override
    public final int unshift(@Nullable V... v) {
        return super.unshift(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A, B> util.@NotNull Collection<A, B> toMap(@NotNull Function<V, Map.Entry<A, B>> function) {
        return super.toMap(function);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A, B> util.@NotNull Collection<A, B> toMap(@NotNull Function<V, A> function1, @NotNull Function<V, B> function2) {
        return super.toMap(function1, function2);
    }
}