package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CollectionList<V> extends ArrayList<V> implements ICollectionList<V>, Cloneable {
    public CollectionList() {
        super();
    }

    public CollectionList(List<? extends V> list) {
        super();
        this.addAll(list);
    }

    @SafeVarargs
    public CollectionList(V... v) {
        super();
        this.addAll(Arrays.asList(v));
    }

    public CollectionList(java.util.Collection<? extends V> list) {
        super();
        this.addAll(list);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Contract("!null -> this")
    @Override
    public CollectionList<V> addChain(@NotNull V v) {
        super.add(v);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public V first() { return this.length() == 0 ? null : this.valuesArray()[0]; }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    @Contract(value = "-> new", pure = true)
    public V[] valuesArray() {
        return (V[]) this.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public V last() { return this.length() == 0 ? null : this.valuesArray()[this.length()-1]; }

    /**
     * {@inheritDoc}
     */
    @Override
    public int length() {
        return this.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void foreach(@NotNull BiConsumer<V, Integer> action) {
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
    public void foreach(@NotNull util.BiBiConsumer<V, Integer, ICollectionList<V>> action) {
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
    public CollectionList<V> reverse() {
        CollectionList<V> target = this.clone();
        Collections.reverse(target);
        return target;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    public CollectionList<V> shuffle() {
        CollectionList<V> target = this.clone();
        Collections.shuffle(target);
        return target;
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
    public CollectionList<V> addAll(@Nullable CollectionList<V> list) {
        if (list == null) return this;
        super.addAll(list);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract("_ -> this")
    public CollectionList<V> putAll(@Nullable CollectionList<V> list) {
        return this.addAll(list);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "!null -> new", pure = true)
    public CollectionList<V> filter(@NotNull Function<V, Boolean> filter) {
        CollectionList<V> newList = new CollectionList<>();
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
    public CollectionList<V> filterNullable(@NotNull Function<V, Boolean> filter) {
        CollectionList<V> newList = new CollectionList<>();
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
    @Contract("-> new")
    @SuppressWarnings("unchecked")
    public CollectionList<V> clone() {
        return (CollectionList<V>) super.clone();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract("!null -> this")
    public CollectionList<V> removeThenReturnCollection(@NotNull V v) {
        this.remove(v);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "!null -> new", pure = true)
    public <T> CollectionList<T> map(@NotNull Function<V, T> function) {
        CollectionList<T> newList = new CollectionList<>();
        this.forEach(v -> newList.add(function.apply(v)));
        return newList;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "!null -> new", pure = true)
    public <T> CollectionList<T> map(@NotNull BiFunction<V, Integer, T> function) {
        CollectionList<T> newList = new CollectionList<>();
        final int[] index = {0};
        this.forEach(v -> {
            newList.add(function.apply(v, index[0]));
            index[0]++;
        });
        return newList;
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
    @Contract(value = "_ -> new", pure = true)
    public CollectionList<V> joinObject(@NotNull V v) {
        if (this.isEmpty()) return this.clone();
        CollectionList<V> list = this.newList();
        this.foreach((a, i) -> {
            if (i != 0) list.add(v);
            list.add(a);
        });
        return list;
    }

    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    public CollectionList<V> newList() {
        return new CollectionList<>();
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
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CollectionList)) return false;
        CollectionList<?> list = (CollectionList<?>) o;
        return super.equals(list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public V shift() {
        if (this.isEmpty()) return null;
        return this.remove(0);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public int unshift(@Nullable V... v) {
        if (v == null || v.length == 0) return this.size();
        this.clone().forEach(this::add);
        for (int i = 0; i < v.length; i++) this.set(i, v[i]);
        return this.size();
    }

    /**
     * {@inheritDoc}
     */
    @SafeVarargs
    @Override
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public final CollectionList<V> concat(CollectionList<V>... lists) {
        if (lists == null) return this.clone();
        CollectionList<V> list = newList();
        list.addAll(this);
        for (CollectionList<V> vs : lists) list.addAll(vs);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    @Contract(value = "-> new", pure = true)
    public CollectionList<V> unique() {
        return new CollectionList<>(new HashSet<>(this.clone()));
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
    public static <T> CollectionList<T> of(T... t) {
        return new CollectionList<>(t);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Contract("-> this")
    public List<V> toList() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "!null -> new", pure = true)
    public <A, B> Collection<A, B> toMap(@NotNull Function<V, Map.Entry<A, B>> function) {
        Collection<A, B> collection = new Collection<>();
        forEach(v -> {
            Map.Entry<A, B> entry = function.apply(v);
            collection.add(entry.getKey(), entry.getValue());
        });
        return collection;
    }
}