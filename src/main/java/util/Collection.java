package util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The Great HashMap
 * @see HashMap The old HashMap (impl)
 * @see Map The old map (interface)
 * @see CollectionSync - for synchronized Collection
 * @see CollectionStrictSync - for strictly synchronized Collection
 */
public class Collection<K, V> extends HashMap<K, V> implements ICollection<K, V> {
    /**
     * Constructs an empty Collection with the default initial capacity (16) and the default load factor (0.75).
     */
    public Collection() {
        super();
    }

    /**
     * Constructs this Collection with values.
     * @param map will be added with this constructor
     */
    public Collection(Map<? extends K, ? extends V> map) {
        super();
        this.addAll(map);
    }

    /**
     * @return first value
     */
    public V first() {
        return this.valuesList().first();
    }

    /**
     * @return first key
     */
    public K firstKey() {
        return this.keysList().first();
    }

    /**
     * @return last value
     */
    public V last() { return this.valuesList().last(); }

    /**
     * @return last key
     */
    public K lastKey() { return this.keysList().last(); }

    /**
     * <b>Note: Unchecked Type Casting</b>
     * @return keys as Array.
     */
    @SuppressWarnings("unchecked")
    public K[] keys() {
        final Object[] a = this.keySet().toArray();
        List<Object> keysObj = Arrays.asList(a);
        CollectionList<K> keys = new CollectionList<>();
        keysObj.forEach(k -> keys.add((K) k));
        return keys.valuesArray();
    }

    /**
     * @return all keys as CollectionList
     */
    public CollectionList<K> keysList() {
        return new CollectionList<>(this.keySet());
    }

    /**
     * @return values as Array.
     */
    @SuppressWarnings("unchecked")
    public V[] valuesArray() {
        return (V[]) this.values().toArray();
    }

    /**
     * @return values as CollectionList.
     */
    public CollectionList<V> valuesList() {
        return new CollectionList<>(this.values());
    }

    /**
     * @param action it passes value, index.
     * @see Collection#foreachKeys(BiConsumer)
     */
    public void foreach(BiConsumer<V, Integer> action) {
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
    public void foreachKeys(BiConsumer<K, Integer> action) {
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
    public V add(K key, V value) {
        return super.put(key, value);
    }

    /**
     * Adds entry by function.<br>
     * When the function returned true, V(v for value) will be added.
     * @deprecated I have no idea why did I add this
     * @param function passes Key, Value. Must return boolean.
     * @return all entries that added from this method
     */
    @Deprecated
    public Collection<K, V> add(BiFunction<K, V, Boolean> function, V v) {
        this.forEach(((k, v2) -> {
            if (function.apply(k, v2)) {
                this.add(k, v);
            }
        }));
        return this;
    }

    /**
     * Adds all entries from provided map.
     * @return this
     */
    public Collection<K, V> addAll(Map<? extends K, ? extends V> map) {
        super.putAll(map);
        return this;
    }

    /**
     * Filters values. If returned true, value will be added to the new Collection. Returns new Collection of filtered values.
     * @see Collection#filterKeys(Function) 
     * @param filter filter function.
     * @return clone of new Collection filtered by function
     */
    public Collection<K, V> filter(Function<V, Boolean> filter) {
        Collection<K, V> newList = new Collection<>();
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
    public Collection<K, V> filterKeys(Function<K, Boolean> filter) {
        Collection<K, V> newList = new Collection<>();
        V[] values = this.valuesArray();
        this.foreachKeys((k, i) -> {
            if (filter.apply(k)) newList.put(k, values[i]);
        });
        return newList;
    }

    /**
     * Removes entry but it returns this collection.
     * @param k will be removed
     * @return this collection
     */
    public Collection<K, V> removeThenReturnCollection(K k) {
        this.remove(k);
        return this;
    }

    /**
     * Clones this collection and returns new collection.
     * @see HashMap#clone()
     * @return New collection
     */
    @Override
    public Collection<K, V> clone() {
        Collection<K, V> newList = new Collection<>();
        newList.addAll(this);
        return newList;
    }

    /**
     * Cast V type to the another type and returns new Collection.
     * @param newType New value type in Class.
     * @param <T> New value type, if it was impossible to cast, ClassCastException will be thrown.
     * @return New collection
     * @throws ClassCastException Thrown when impossible to cast.
     */
    public <T> Collection<K, T> cast(Class<T> newType) {
        Collection<K, T> collection = new Collection<>();
        this.forEach((k, v) -> collection.add(k, newType.cast(v)));
        return collection;
    }

    /**
     * Returns all values that matches with V.
     * @return new collection
     */
    public Collection<K, V> values(V v) {
        return new Collection<>(this.filter(f -> f.equals(v)));
    }
}
