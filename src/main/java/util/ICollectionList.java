package util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ICollectionList<V> {
    V first();
    V[] valuesArray();
    V last();
    void foreach(BiConsumer<V, Integer> action);
    V put(V v);
    ICollectionList<V> reverse();
    ICollectionList<V> shuffle();
    <ListLike extends List<? extends V>> void putAll(ListLike list);
    ICollectionList<V> addAll(CollectionList<V> list);
    ICollectionList<V> putAll(CollectionList<V> list);
    ICollectionList<V> filter(Function<V, Boolean> filter);
    ICollectionList<V> clone();
    ICollectionList<V> removeThenReturnCollection(V v);
    <T> ICollectionList<T> cast(Class<T> t);
    <T> ICollectionList<T> map(Function<V, T> function);
    <T> ICollectionList<T> map(BiFunction<V, Integer, T> function);

    static <T> ICollectionList<T> fromValues(Map<?, ? extends T> map) {
        return new CollectionList<>(map.values());
    }

    static <T> ICollectionList<T> fromKeys(Map<? extends T, ?> map) {
        return new CollectionList<>(map.keySet());
    }

    static <T> ICollectionList<T> asList(List<? extends T> list) {
        return new CollectionList<>(list);
    }

    static <T> ICollectionList<T> asList(T[] list) {
        CollectionList<T> collectionList = new CollectionList<>();
        collectionList.addAll(Arrays.asList(list));
        return collectionList;
    }

    /**
     * Casts type to another. Exactly same method as CollectionList#cast().
     * @param <T> New value type, if it was impossible to cast, ClassCastException will be thrown.
     * @return New collection
     * @throws ClassCastException Thrown when impossible to cast
     */
    static <T> ICollectionList<T> cast(CollectionList<?> l, Class<T> t) {
        CollectionList<T> list = new CollectionList<>();
        l.forEach(v -> list.add(t.cast(v)));
        return list;
    }
}
