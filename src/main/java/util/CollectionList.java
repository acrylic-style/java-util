package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CollectionList<V> extends ArrayList<V> implements ICollectionList<V> {
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

    @Override
    public V first() { return this.valuesArray()[0]; }

    @Override
    @SuppressWarnings("unchecked")
    public V[] valuesArray() {
        return (V[]) this.toArray();
    }

    @Override
    public V last() { return this.valuesArray()[0]; }

    @Override
    public int length() {
        return this.size();
    }

    @Override
    public void foreach(BiConsumer<V, Integer> action) {
        final int[] index = {0};
        this.forEach(v -> {
            action.accept(v, index[0]);
            index[0]++;
        });
    }

    @Override
    public void foreach(util.BiBiConsumer<V, Integer, ICollectionList<V>> action) {
        final int[] index = {0};
        this.forEach(v -> {
            action.accept(v, index[0], this.clone());
            index[0]++;
        });
    }

    @Override
    public V put(V value) {
        super.add(value);
        return value;
    }

    @Override
    public CollectionList<V> reverse() {
        CollectionList<V> target = this.clone();
        Collections.reverse(target);
        return target;
    }

    @Override
    public CollectionList<V> shuffle() {
        CollectionList<V> target = this.clone();
        Collections.shuffle(target);
        return target;
    }

    @Override
    public <ListLike extends List<? extends V>> void putAll(ListLike list) {
        super.addAll(list);
    }

    @Override
    public CollectionList<V> addAll(CollectionList<V> list) {
        list.forEach(this::add);
        return this;
    }

    @Override
    public CollectionList<V> putAll(CollectionList<V> list) {
        return this.addAll(list);
    }

    @Override
    public CollectionList<V> filter(Function<V, Boolean> filter) {
        CollectionList<V> newList = new CollectionList<>();
        this.foreach((v, i) -> {
            if (filter.apply(v)) newList.add(v);
        });
        return newList;
    }

    @Override
    public CollectionList<V> filterNullable(Function<V, Boolean> filter) {
        CollectionList<V> newList = new CollectionList<>();
        this.foreach((v, i) -> {
            if (filter.apply(v)) newList.add(v);
        });
        return newList.size() == 0 ? null : newList;
    }

    @Override
    public CollectionList<V> clone() {
        CollectionList<V> newList = new CollectionList<>();
        newList.addAll(this);
        return newList;
    }

    @Override
    public CollectionList<V> removeThenReturnCollection(V v) {
        this.remove(v);
        return this;
    }

    @Override
    public <T> CollectionList<T> map(Function<V, T> function) {
        CollectionList<T> newList = new CollectionList<>();
        this.forEach(v -> newList.add(function.apply(v)));
        return newList;
    }

    @Override
    public <T> CollectionList<T> map(BiFunction<V, Integer, T> function) {
        CollectionList<T> newList = new CollectionList<>();
        final int[] index = {0};
        this.forEach(v -> {
            newList.add(function.apply(v, index[0]));
            index[0]++;
        });
        return newList;
    }

    @Override
    public String join(String s) {
        if (this.isEmpty()) return "";
        StringBuilder str = new StringBuilder();
        this.foreach((a, i) -> {
            if (i != 0) str.append(s == null ? "," : s);
            str.append(a);
        });
        return str.toString();
    }

    @Override
    public String join() {
        return this.join(null);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CollectionList)) return false;
        CollectionList<?> list = (CollectionList<?>) o;
        return super.equals(list);
    }

    @Override
    public V shift() {
        if (this.isEmpty()) return null;
        return this.remove(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int unshift(V... v) {
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
    public final CollectionList<V> concat(CollectionList<V>... lists) {
        if (lists == null) return this.clone();
        CollectionList<V> list = this.clone();
        for (CollectionList<V> vs : lists) list.addAll(vs);
        return list;
    }

    @Override
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

    public List<V> toList() {
        return this;
    }
}