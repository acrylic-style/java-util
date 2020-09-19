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

    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    public CollectionList<V> newList() {
        return new CollectionList<>();
    }

    @Override
    public @NotNull AbstractArrayCollectionList<V> newList(Collection<? extends V> list) {
        return new CollectionList<>(list);
    }

    @Override
    public @NotNull <E> CollectionList<E> createList() { return new CollectionList<>(); }

    @SafeVarargs
    @Override
    public @NotNull
    final CollectionList<V> concat(ICollectionList<V>... lists) {
        return (CollectionList<V>) super.concat(lists);
    }

    @Override
    public @NotNull CollectionList<V> unique() {
        return (CollectionList<V>) super.unique();
    }

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

    @Override
    public @NotNull CollectionList<V> addChain(@NotNull V v) {
        return (CollectionList<V>) super.addChain(v);
    }

    @Override
    public @NotNull CollectionList<V> reverse() {
        return (CollectionList<V>) super.reverse();
    }

    @Override
    public @NotNull <T> CollectionList<T> map(@NotNull Function<V, T> function) {
        return (CollectionList<T>) super.map(function);
    }

    @Override
    public @NotNull <T> CollectionList<T> map(@NotNull BiFunction<V, Integer, T> function) {
        return (CollectionList<T>) super.map(function);
    }

    @Override
    public @NotNull CollectionList<V> deepClone() {
        return (CollectionList<V>) super.deepClone();
    }

    @Override
    public @NotNull CollectionList<V> joinObject(@NotNull V v) {
        return (CollectionList<V>) super.joinObject(v);
    }

    @Override
    public @NotNull CollectionList<V> shuffle() {
        return (CollectionList<V>) super.shuffle();
    }

    @Override
    public @NotNull CollectionList<V> addAll(@Nullable ICollectionList<V> list) {
        return (CollectionList<V>) super.addAll(list);
    }

    @Override
    public @NotNull CollectionList<V> putAll(@Nullable ICollectionList<V> list) {
        return (CollectionList<V>) super.putAll(list);
    }

    @Override
    public @NotNull CollectionList<V> filter(@NotNull Function<V, Boolean> filter) {
        return (CollectionList<V>) super.filter(filter);
    }

    @Override
    public @NotNull CollectionList<V> removeThenReturnCollection(@NotNull V v) {
        return (CollectionList<V>) super.removeThenReturnCollection(v);
    }

    @Override
    public @NotNull CollectionList<V> shiftChain() {
        return (CollectionList<V>) super.shiftChain();
    }

    @Override
    public @NotNull CollectionList<V> nonNull() {
        return (CollectionList<V>) super.nonNull();
    }

    // These methods are just overriding, to prevent NoSuchMethodError on old code

    @Override
    public @NotNull List<V> toList() {
        return super.toList();
    }

    @Override
    public @Range(from = 0, to = 1) double distribution(@NotNull V v) {
        return super.distribution(v);
    }

    @Override
    public Map.Entry<Double, Integer> distributionEntry(@NotNull V v) {
        return super.distributionEntry(v);
    }

    @Nullable
    @Override
    public V first() {
        return super.first();
    }

    @NotNull
    @Override
    public V[] valuesArray() {
        return super.valuesArray();
    }

    @Nullable
    @Override
    public V last() {
        return super.last();
    }

    @Override
    public int length() {
        return super.length();
    }

    @Override
    public void foreach(@NotNull BiConsumer<V, Integer> action) {
        super.foreach(action);
    }

    @Override
    public void foreach(@NotNull BiBiConsumer<V, Integer, ICollectionList<V>> action) {
        super.foreach(action);
    }

    @NotNull
    @Override
    public V put(@NotNull V value) {
        return super.put(value);
    }

    @Override
    public <ListLike extends List<? extends V>> void putAll(@NotNull ListLike list) {
        super.putAll(list);
    }

    @Override
    public @NotNull String join(String s) {
        return super.join(s);
    }

    @Override
    public @NotNull String join() {
        return super.join();
    }

    @Nullable
    @Override
    public V shift() {
        return super.shift();
    }

    @SafeVarargs
    @Override
    public final int unshift(@Nullable V... v) {
        return super.unshift(v);
    }

    @Override
    public <A, B> util.@NotNull Collection<A, B> toMap(@NotNull Function<V, Map.Entry<A, B>> function) {
        return super.toMap(function);
    }

    @Override
    public <A, B> util.@NotNull Collection<A, B> toMap(@NotNull Function<V, A> function1, @NotNull Function<V, B> function2) {
        return super.toMap(function1, function2);
    }
}