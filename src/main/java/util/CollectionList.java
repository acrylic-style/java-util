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

    public CollectionList<V> removeThenReturnCollection(V v) {
        this.remove(v);
        return this;
    }

    /**
     * Casts type to another. Exactly same method as ICollectionList.cast().
     * @param <T> New value type, if it was impossible to cast, ClassCastException will be thrown.
     * @return New collection
     * @throws ClassCastException Thrown when impossible to cast
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> CollectionList<T> cast(Class<T> t) {
        if (t == String.class) {
            CollectionList<String> list = new CollectionList<>();
            this.forEach(v -> list.add(v instanceof Enum ? ((Enum) v).name() : v.toString()));
            return (CollectionList<T>) list;
        }
        CollectionList<T> list = new CollectionList<>();
        this.forEach(v -> list.add(t.cast(v)));
        return list;
    }

    /**
     * The <b>map()</b> method <b>creates a new array</b> populated with the results of calling a provided function on every element in the calling array.
     * @param function Function that will run to create a new array.
     * @return New array with new type.
     */
    public <T> CollectionList<T> map(Function<V, T> function) {
        CollectionList<T> newList = new CollectionList<>();
        this.forEach(v -> newList.add(function.apply(v)));
        return newList;
    }

    /**
     * The <b>map()</b> method <b>creates a new array</b> populated with the results of calling a provided function on every element in the calling array.
     * @param function Function that will run to create a new array.
     * @return New array with new type.
     */
    public <T> CollectionList<T> map(BiFunction<V, Integer, T> function) {
        CollectionList<T> newList = new CollectionList<>();
        final int[] index = {0};
        this.forEach(v -> {
            newList.add(function.apply(v, index[0]));
            index[0]++;
        });
        return newList;
    }
}