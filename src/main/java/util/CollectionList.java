package util;

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
    public void foreach(BiConsumer<V, Integer> action) {
        final int[] index = {0};
        this.forEach(v -> {
            action.accept(v, index[0]);
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
            str.append(a);
            if (i != 0) str.append(s);
        });
        return str.toString();
    }
}