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
    ICollectionList<K> keysList();
    V[] valuesArray();
    ICollectionList<V> valuesList();
    void foreach(BiConsumer<V, Integer> action);
    void foreachKeys(BiConsumer<K, Integer> action);
    ICollection<K, V> addAll(Map<? extends K, ? extends V> map);
    ICollection<K, V> filter(Function<V, Boolean> filter);
    ICollection<K, V> filterKeys(Function<K, Boolean> filter);
    ICollection<K, V> removeThenReturnCollection(K k);
    ICollection<K, V> clone();
    <T> ICollection<K, T> cast(Class<T> newType);
    ICollection<K, V> values(V v);
    <A, B> ICollection<A, B> map(BiFunction<K, V, A> keyFunction, BiFunction<K, V, B> valueFunction);

    static <K, V> ICollection<K, V> asCollection(Map<? extends K, ? extends V> map) {
        Collection<K, V> collection = new Collection<>();
        collection.addAll(map);
        return collection;
    }

    static <K, V> ICollection<K, V> asCollectionSync(Map<? extends K, ? extends V> map) {
        CollectionSync<K, V> collection = new CollectionSync<>();
        collection.addAll(map);
        return collection;
    }

    static <K, V> ICollection<K, V> asCollectionStrictSync(Map<? extends K, ? extends V> map) {
        CollectionStrictSync<K, V> collection = new CollectionStrictSync<>();
        collection.addAll(map);
        return collection;
    }
}
