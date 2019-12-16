package util;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface ICollectionList<V> {
    V first();
    V[] valuesArray();
    V last();
    void foreach(BiConsumer<V, Integer> action);
    V put(V v);
    CollectionList<V> reverse();
    CollectionList<V> shuffle();
    <ListLike extends List<? extends V>> void putAll(ListLike list);
    CollectionList<V> addAll(CollectionList<V> list);
    CollectionList<V> putAll(CollectionList<V> list);
    CollectionList<V> filter(Function<V, Boolean> filter);
    CollectionList<V> clone();
    CollectionList<V> removeThenReturnCollection(V v);
    <T> CollectionList<T> cast(Class<T> t);

    static <T> CollectionList<T> fromValues(Map<?, ? extends T> map) {
        return new CollectionList<>(map.values());
    }

    static <T> CollectionList<T> fromKeys(Map<? extends T, ?> map) {
        return new CollectionList<>(map.keySet());
    }

    static <T> CollectionList<T> asList(List<? extends T> list) {
        return new CollectionList<>(list);
    }

    /**
     * Casts type to another. Exactly same method as CollectionList#cast().
     * @param <T> New value type, if it was impossible to cast, ClassCastException will be thrown.
     * @return New collection
     * @throws ClassCastException Thrown when impossible to cast
     */
    static <T> CollectionList<T> cast(CollectionList<?> l, Class<T> t) {
        CollectionList<T> list = new CollectionList<>();
        l.forEach(v -> list.add(t.cast(v)));
        return list;
    }
}
