package util;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ICollection<K, V> {
    V first();
    K firstKey();
    V last();
    K lastKey();
    K[] keys();
    CollectionList<K> keysList();
    V[] valuesArray();
    CollectionList<V> valuesList();
    void foreach(BiConsumer<V, Integer> action);
    void foreachKeys(BiConsumer<K, Integer> action);
    @Deprecated
    Collection<K, V> add(BiFunction<K, V, Boolean> function, V v);
    Collection<K, V> addAll(Map<? extends K, ? extends V> map);
    Collection<K, V> filter(Function<V, Boolean> filter);
    Collection<K, V> filterKeys(Function<K, Boolean> filter);
    Collection<K, V> removeThenReturnCollection(K k);
    Collection<K, V> clone();
    <T> Collection<K, T> cast(Class<T> newType);
    Collection<K, V> values(V v);
    <A, B> Collection<A, B> map(BiFunction<K, V, A> keyFunction, BiFunction<K, V, B> valueFunction);

    static <K, V> Collection<K, V> asCollection(Map<? extends K, ? extends V> map) {
        Collection<K, V> collection = new Collection<>();
        collection.addAll(map);
        return collection;
    }

    static <K, V> Collection<K, V> asCollectionSync(Map<? extends K, ? extends V> map) {
        CollectionSync<K, V> collection = new CollectionSync<>();
        collection.addAll(map);
        return collection;
    }

    static <K, V> Collection<K, V> asCollectionStrictSync(Map<? extends K, ? extends V> map) {
        CollectionStrictSync<K, V> collection = new CollectionStrictSync<>();
        collection.addAll(map);
        return collection;
    }
}
