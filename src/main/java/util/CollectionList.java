package util;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CollectionList<V> extends ArrayList<V> {
    public CollectionList() {
        super();
    }

    public CollectionList(ArrayList<? extends V> list) {
        super();
        this.addAll(list);
    }

    public CollectionList(java.util.Collection<? extends V> list) {
        super();
        this.addAll(list);
    }

    public CollectionList(CollectionList<? extends V> list) {
        super();
        this.addAll(list);
    }

    public CollectionList(Set<? extends V> list) {
        super();
        this.addAll(list);
    }

    public V first() { return this.valuesArray()[0]; }

    @SuppressWarnings("unchecked")
    public V[] valuesArray() {
        return (V[]) this.toArray();
    }

    public V last() { return this.valuesArray()[0]; }

    /**
     * @param action it passes value, index.
     */
    public void foreach(BiConsumer<V, Integer> action) {
        final int[] index = {0};
        this.forEach(v -> {
            action.accept(v, index[0]);
            index[0]++;
        });
    }

    public V put(V value) {
        super.add(value);
        return value;
    }

    public CollectionList<V> reverse() {
        CollectionList<V> target = this.clone();
        Collections.reverse(target);
        return target;
    }

    /**
     * Shuffles all entries in list.
     * @return shuffled new list
     */
    public CollectionList<V> shuffle() {
        CollectionList<V> target = this.clone();
        Collections.shuffle(target);
        return target;
    }

    public <ListLike extends List<? extends V>> void putAll(ListLike list) {
        super.addAll(list);
    }

    public CollectionList<V> addAll(CollectionList<V> list) {
        list.forEach(this::add);
        return this;
    }

    public CollectionList<V> putAll(CollectionList<V> list) {
        return this.addAll(list);
    }

    /**
     * Filters values. If returned true, that value will be kept. Returns new Collection of filtered values.
     * @param filter filter function.
     */
    public CollectionList<V> filter(Function<V, Boolean> filter) {
        CollectionList<V> newList = new CollectionList<>();
        this.foreach((v, i) -> {
            if (filter.apply(v)) newList.add(v);
        });
        return newList;
    }

    public CollectionList<V> clone() {
        CollectionList<V> newList = new CollectionList<>();
        newList.addAll(this);
        return newList;
    }

    public CollectionList<V> removeReturnCollection(V v) {
        this.remove(v);
        return this;
    }

    public static <T> CollectionList<T> fromValues(HashMap<?, ? extends T> map) {
        return new CollectionList<>(map.values());
    }

    public static <T> CollectionList<T> fromKeys(HashMap<? extends T, ?> map) {
        return new CollectionList<>(map.keySet());
    }
}