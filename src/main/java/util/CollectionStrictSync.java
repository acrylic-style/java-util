package util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * CollectionSync but all methods are synchronized. The lock is shared across all methods.<br>
 * It may cause huge lags when doing large operations.
 * @see CollectionSync
 * @see Collection
 * @see HashMap
 * @see Map
 */
public class CollectionStrictSync<K, V> extends CollectionSync<K, V> {
    /**
     * Constructs an empty Collection with the default initial capacity (16) and the default load factor (0.75).
     */
    public CollectionStrictSync() {}

    /**
     * Constructs this Collection with values.
     * @param map will be added with this constructor
     */
    public CollectionStrictSync(Map<? extends K, ? extends V> map) {
        synchronized (StrictLock.LOCK) {
            this.addAll(map);
        }
    }

    /**
     * @return first value
     */
    @Override
    public synchronized V first() {
        synchronized (StrictLock.LOCK) {
            return this.valuesArray()[0];
        }
    }

    /**
     * @return first key
     */
    @Override
    public synchronized K firstKey() {
        synchronized (StrictLock.LOCK) {
            return this.keys()[0];
        }
    }

    /**
     * @return last value
     */
    public V last() {
        synchronized (StrictLock.LOCK) {
            return this.valuesArray()[this.size()-1];
        }
    }

    /**
     * @return last key
     */
    public K lastKey() {
        synchronized (StrictLock.LOCK) {
            return this.keys()[this.size()-1];
        }
    }

    /**
     * @return keys as Array.
     */
    @Override
    @SuppressWarnings("unchecked")
    public synchronized K[] keys() {
        synchronized (StrictLock.LOCK) {
            return (K[]) this.keySet().toArray();
        }
    }

    /**
     * @return all keys as CollectionList. <b>CollectionList isn't synchronized!</b>
     */
    @Override
    public synchronized CollectionList<K> keysList() {
        synchronized (StrictLock.LOCK) {
            return new CollectionList<>(this.keySet());
        }
    }

    /**
     * @return values as Array.
     */
    @Override
    @SuppressWarnings("unchecked")
    public synchronized V[] valuesArray() {
        synchronized (StrictLock.LOCK) {
            return (V[]) this.values().toArray();
        }
    }

    /**
     * @return values as CollectionList. <b>CollectionList isn't synchronized!</b>
     */
    @Override
    public synchronized CollectionList<V> valuesList() {
        synchronized (StrictLock.LOCK) {
            return new CollectionList<>(this.values());
        }
    }

    /**
     * @param action it passes value, index.
     * @see Collection#foreachKeys(BiConsumer)
     */
    @Override
    public synchronized void foreach(BiConsumer<V, Integer> action) {
        synchronized (StrictLock.LOCK) {
            final int[] index = {0};
            this.values().forEach(v -> {
                action.accept(v, index[0]);
                index[0]++;
            });
        }
    }

    /**
     * @param action it passes key, index.
     * @see Collection#foreach(BiConsumer)
     */
    @Override
    public synchronized void foreachKeys(BiConsumer<K, Integer> action) {
        synchronized (StrictLock.LOCK) {
            final int[] index = {0};
            this.keySet().forEach(k -> {
                action.accept(k, index[0]);
                index[0]++;
            });
        }
    }

    /**
     * Adds key-value to the Collection.
     * @return added value
     */
    @Override
    public synchronized V add(K key, V value) {
        synchronized (StrictLock.LOCK) {
            return super.put(key, value);
        }
    }

    /**
     * Adds multiple entry by function.<br>
     * When the function returned true, V(v for value) will be added.
     * @param function passes Key, Value. Must return boolean.
     * @return all entries that added from this method
     */
    @Override
    public synchronized CollectionStrictSync<K, V> add(BiFunction<K, V, Boolean> function, V v) {
        synchronized (StrictLock.LOCK) {
            this.forEach(((k, v2) -> {
                if (function.apply(k, v2)) {
                    this.add(k, v);
                }
            }));
            return this;
        }
    }

    /**
     * Adds all entries from provided map.
     * @return this
     */
    @Override
    public synchronized CollectionStrictSync<K, V> addAll(Map<? extends K, ? extends V> map) {
        synchronized (StrictLock.LOCK) {
            super.putAll(map);
            return this;
        }
    }

    /**
     * Filters values. If returned true, value will be added to the new Collection. Returns new Collection of filtered values.
     * @see Collection#filterKeys(Function)
     * @param filter filter function.
     * @return clone of new Collection filtered by function
     */
    @Override
    public synchronized CollectionStrictSync<K, V> filter(Function<V, Boolean> filter) {
        synchronized (StrictLock.LOCK) {
            CollectionStrictSync<K, V> newList = new CollectionStrictSync<>();
            K[] keys = this.keys();
            this.foreach((v, i) -> {
                if (filter.apply(v)) newList.put(keys[i], v);
            });
            return newList.clone();
        }
    }

    /**
     * Filters keys. If returned true, value will be added to the new Collection. Returns new Collection of filtered values.
     * @see Collection#filter(Function)
     * @param filter filter function.
     * @return clone of new Collection filtered by function
     */
    @Override
    public synchronized CollectionStrictSync<K, V> filterKeys(Function<K, Boolean> filter) {
        synchronized (StrictLock.LOCK) {
            CollectionStrictSync<K, V> newList = new CollectionStrictSync<>();
            V[] values = this.valuesArray();
            this.foreachKeys((k, i) -> {
                if (filter.apply(k)) newList.put(k, values[i]);
            });
            return newList.clone();
        }
    }

    /**
     * Removes entry but it returns this collection.
     * @param k will be removed
     * @return this collection
     */
    @Override
    public synchronized CollectionStrictSync<K, V> removeThenReturnCollection(K k) {
        synchronized (StrictLock.LOCK) {
            this.remove(k);
            return this;
        }
    }

    /**
     * Clones this collection and returns new collection.
     * @see HashMap#clone()
     * @return new collection
     */
    @Override
    public synchronized CollectionStrictSync<K, V> clone() {
        synchronized (StrictLock.LOCK) {
            CollectionStrictSync<K, V> newList = new CollectionStrictSync<>();
            newList.addAll(this);
            return newList;
        }
    }

    @Override
    public <T> Collection<K, T> cast(Class<T> newType) throws ClassCastException {
        synchronized (StrictLock.LOCK) {
            Collection<K, T> collection = new Collection<>();
            this.forEach((k, v) -> collection.add(k, newType.cast(v)));
            return collection;
        }
    }

    /**
     * Returns all values that matches with V.
     * @return new collection
     */
    @Override
    public synchronized CollectionStrictSync<K, V> values(V v) {
        synchronized (StrictLock.LOCK) {
            return new CollectionStrictSync<>(this.filter(f -> f.equals(v)));
        }
    }
}

class StrictLock {
    /**
     * for synchronized constructor
     */
    final static Object LOCK = new Object();
}
