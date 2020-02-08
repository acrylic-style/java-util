package util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Collection but all methods(including constructor) are synchronized.<br>
 * It may cause small lags when doing large operations.
 * @see CollectionStrictSync
 * @see Collection
 * @see HashMap
 * @see Map
 */
public class CollectionSync<K, V> extends Collection<K, V> {
    /**
     * Constructs an empty Collection with the default initial capacity (16) and the default load factor (0.75).
     */
    public CollectionSync() {}

    /**
     * Constructs this Collection with values.
     * @param map will be added with this constructor
     */
    public CollectionSync(Map<? extends K, ? extends V> map) {
        synchronized (Lock.LOCK) {
            this.addAll(map);
        }
    }

    /**
     * @return first value
     */
    @Override
    public synchronized V first() {
        return this.valuesArray()[0];
    }

    /**
     * @return first key
     */
    @Override
    public synchronized K firstKey() {
        return this.keys()[0];
    }

    /**
     * @return last value
     */
    public synchronized V last() { return this.valuesArray()[this.size()-1]; }

    /**
     * @return last key
     */
    public synchronized K lastKey() { return this.keys()[this.size()-1]; }

    /**
     * @return keys as Array.
     */
    @Override
    @SuppressWarnings("unchecked")
    public synchronized K[] keys() {
        return (K[]) this.keySet().toArray();
    }

    /**
     * @return all keys as CollectionList. <b>CollectionList isn't synchronized!</b>
     */
    @Override
    public synchronized CollectionList<K> keysList() {
        return new CollectionList<>(this.keySet());
    }

    /**
     * @return values as Array.
     */
    @Override
    @SuppressWarnings("unchecked")
    public synchronized V[] valuesArray() {
        return (V[]) this.values().toArray();
    }

    /**
     * @return values as CollectionList. <b>CollectionList isn't synchronized!</b>
     */
    @Override
    public synchronized CollectionList<V> valuesList() {
        return new CollectionList<>(this.values());
    }

    /**
     * @param action it passes value, index.
     * @see Collection#foreachKeys(BiConsumer)
     */
    @Override
    public synchronized void foreach(BiConsumer<V, Integer> action) {
        final int[] index = {0};
        this.values().forEach(v -> {
            action.accept(v, index[0]);
            index[0]++;
        });
    }

    /**
     * @param action it passes key, index.
     * @see Collection#foreach(BiConsumer)
     */
    @Override
    public synchronized void foreachKeys(BiConsumer<K, Integer> action) {
        final int[] index = {0};
        this.keySet().forEach(k -> {
            action.accept(k, index[0]);
            index[0]++;
        });
    }

    /**
     * Adds key-value to the Collection.
     * @return added value
     */
    @Override
    public synchronized V add(K key, V value) {
        return super.put(key, value);
    }

    /**
     * Adds all entries from provided map.
     * @return this
     */
    @Override
    public synchronized CollectionSync<K, V> addAll(Map<? extends K, ? extends V> map) {
        super.putAll(map);
        return this;
    }

    /**
     * Filters values. If returned true, value will be added to the new Collection. Returns new Collection of filtered values.
     * @see Collection#filterKeys(Function)
     * @param filter filter function.
     * @return clone of new Collection filtered by function
     */
    @Override
    public synchronized CollectionSync<K, V> filter(Function<V, Boolean> filter) {
        CollectionSync<K, V> newList = new CollectionSync<>();
        K[] keys = this.keys();
        this.foreach((v, i) -> {
            if (filter.apply(v)) newList.put(keys[i], v);
        });
        return newList.clone();
    }

    /**
     * Filters keys. If returned true, value will be added to the new Collection. Returns new Collection of filtered values.
     * @see Collection#filter(Function)
     * @param filter filter function.
     * @return clone of new Collection filtered by function
     */
    @Override
    public synchronized CollectionSync<K, V> filterKeys(Function<K, Boolean> filter) {
        CollectionSync<K, V> newList = new CollectionSync<>();
        V[] values = this.valuesArray();
        this.foreachKeys((k, i) -> {
            if (filter.apply(k)) newList.put(k, values[i]);
        });
        return newList.clone();
    }

    /**
     * Removes entry but it returns this collection.
     * @param k will be removed
     * @return this collection
     */
    @Override
    public synchronized CollectionSync<K, V> removeThenReturnCollection(K k) {
        this.remove(k);
        return this;
    }

    /**
     * Clones this collection and returns new collection.
     * @see HashMap#clone()
     * @return new collection
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public synchronized CollectionSync<K, V> clone() {
        CollectionSync<K, V> newList = new CollectionSync<>();
        newList.addAll(this);
        return newList;
    }

    @Override
    public synchronized <T> Collection<K, T> cast(Class<T> newType) throws ClassCastException {
        Collection<K, T> collection = new Collection<>();
        this.forEach((k, v) -> collection.add(k, newType.cast(v)));
        return collection;
    }

    /**
     * Returns all values that matches with V.
     * @return new collection
     */
    @Override
    public synchronized CollectionSync<K, V> values(V v) {
        return new CollectionSync<>(this.filter(f -> f.equals(v)));
    }
}

class Lock {
    /**
     * for synchronized constructor
     */
    final static Object LOCK = new Object();
}
