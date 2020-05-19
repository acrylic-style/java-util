package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("NullableProblems")
public class CollectionSet<V> extends HashSet<V> implements ICollectionList<V>, Cloneable {
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

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Contract("!null -> this")
    @Override
    public CollectionSet<V> addChain(@NotNull V v) {
        super.add(v);
        return this.unique();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public V first() { return this.unique().length() == 0 ? null : this.unique().valuesArray()[0]; }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    @Contract(value = "-> new", pure = true)
    public V[] valuesArray() {
        return (V[]) this.unique().toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
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
    @Contract("!null -> param1")
    public V put(@NotNull V value) {
        super.add(value);
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    public CollectionSet<V> reverse() {
        CollectionSet<V> target = this.clone();
        Collections.reverse(target);
        return target.unique();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    public CollectionSet<V> shuffle() {
        CollectionSet<V> target = this.clone();
        Collections.shuffle(target);
        return target.unique();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <ListLike extends List<? extends V>> void putAll(@NotNull ListLike list) {
        super.addAll(list);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract("_ -> this")
    public CollectionSet<V> addAll(@Nullable ICollectionList<V> list) {
        if (list == null) return this;
        super.addAll(list);
        return this.unique();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract("_ -> this")
    public CollectionSet<V> putAll(@Nullable ICollectionList<V> list) {
        return this.addAll(list).unique();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "!null -> new", pure = true)
    public CollectionSet<V> filter(@NotNull Function<V, Boolean> filter) {
        CollectionSet<V> newList = new CollectionSet<>();
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
        CollectionSet<V> newList = new CollectionSet<>();
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
        return ((CollectionSet<V>) super.clone());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "!null -> this")
    public CollectionSet<V> removeThenReturnCollection(@NotNull V v) {
        this.remove(v);
        return this.unique();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "!null -> new", pure = true)
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
    @Contract(value = "!null -> new", pure = true)
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
        if (this.isEmpty()) return this.clone();
        CollectionSet<V> list = this.newList();
        this.foreach((a, i) -> {
            if (i != 0) list.add(v);
            list.add(a);
        });
        return list.unique();
    }

    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    public CollectionSet<V> newList() {
        return new CollectionSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    public String join() {
        return this.unique().join(null);
    }

    @Contract("_, _ -> fail")
    @Override
    public boolean addAll(int index, @NotNull java.util.Collection<? extends V> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CollectionSet)) return false;
        CollectionSet<?> list = (CollectionSet<?>) o;
        return super.equals(list.unique());
    }

    @Contract("_ -> fail")
    @Override
    public V get(int index) {
        throw new UnsupportedOperationException();
    }

    @Contract("_, _ -> fail")
    @Override
    public V set(int index, V element) {
        throw new UnsupportedOperationException();
    }

    @Contract("_, _ -> fail")
    @Override
    public void add(int index, V element) {
        throw new UnsupportedOperationException();
    }

    @Contract("_ -> fail")
    @Override
    public V remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Contract("_ -> fail")
    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Contract("_ -> fail")
    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    @Contract("-> fail")
    public ListIterator<V> listIterator() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    @Contract("_ -> fail")
    public ListIterator<V> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    @Contract("_, _ -> fail")
    public List<V> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Contract("-> fail")
    @Override
    @Nullable
    public V shift() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Contract("_ -> fail")
    @Override
    public int unshift(@Nullable V... v) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @SafeVarargs
    @Override
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public final CollectionSet<V> concat(ICollectionList<V>... lists) {
        if (lists == null) return this.clone();
        CollectionSet<V> list = newList();
        list.addAll(this);
        for (ICollectionList<V> vs : lists) list.addAll(vs);
        return list.unique();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    @Contract(value = "-> new", pure = true)
    public CollectionSet<V> unique() {
        return new CollectionSet<>(new HashSet<>(this.clone()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    @Contract(value = "-> new", pure = true)
    public CollectionSet<V> nonNull() {
        return new CollectionSet<>(this.clone().unique().filter(Objects::nonNull));
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
     */
    @NotNull
    @Contract("-> this")
    public List<V> toList() {
        return this.unique();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "!null -> new", pure = true)
    public <A, B> Collection<A, B> toMap(@NotNull Function<V, Map.Entry<A, B>> function) {
        Collection<A, B> collection = new Collection<>();
        this.unique().forEach(v -> {
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
    @Contract(value = "!null, !null -> new", pure = true)
    public <A, B> ICollection<A, B> toMap(@NotNull Function<V, A> function1, @NotNull Function<V, B> function2) {
        Collection<A, B> collection = new Collection<>();
        this.unique().forEach(v -> collection.add(function1.apply(v), function2.apply(v)));
        return collection;
    }
}