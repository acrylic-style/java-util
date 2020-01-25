package util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The Great HashMap
 * @see HashMap The old HashMap (impl)
 * @see Map The old map (interface)
 * @see CollectionSync - for synchronized Collection
 * @see CollectionStrictSync - for strictly synchronized Collection
 */
public class StringCollection<V> extends Collection<String, V> implements ICollection<String, V> {
    /**
     * Constructs an empty Collection with the default initial capacity (16) and the default load factor (0.75).
     */
    public StringCollection() {
        super();
    }

    /**
     * Constructs this Collection with values.
     * @param map will be added with this constructor
     */
    public StringCollection(Map<? extends String, ? extends V> map) {
        super();
        this.addAll(map);
    }

    /**
     * @return first value
     */
    @Override
    public V first() {
        return this.valuesList().first();
    }

    /**
     * @return first key
     */
    @Override
    public String firstKey() {
        return this.keysList().first();
    }

    /**
     * @return last value
     */
    @Override
    public V last() { return this.valuesList().last(); }

    /**
     * @return last key
     */
    @Override
    public String lastKey() { return this.keysList().last(); }

    /**
     * @return keys as Array.
     */
    @Override
    public String[] keys() {
        final Object[] a = this.keySet().toArray();
        List<Object> keysObj = Arrays.asList(a);
        CollectionList<String> keys = new CollectionList<>();
        keysObj.forEach(k -> keys.add((String) k));
        return keys.valuesArray();
    }

    /**
     * @return all keys as CollectionList
     */
    @Override
    public CollectionList<String> keysList() {
        return new CollectionList<>(this.keySet());
    }

    /**
     * @return values as Array.
     */
    @Override
    @SuppressWarnings("unchecked")
    public V[] valuesArray() {
        return (V[]) this.values().toArray();
    }

    /**
     * @return values as CollectionList.
     */
    @Override
    public CollectionList<V> valuesList() {
        return new CollectionList<>(this.values());
    }

    /**
     * @param action it passes value, index.
     * @see Collection#foreachKeys(BiConsumer)
     */
    @Override
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
    @Override
    public void foreachKeys(BiConsumer<String, Integer> action) {
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
    public V add(String key, V value) {
        return super.put(key, value);
    }

    private V find(String key) {
        CollectionList<String> list = this.keysList().filterNullable(str -> str.equals(key));
        if (list == null) return null;
        return super.get(list.first());
    }

    @Override
    public V get(Object key) {
        V v = super.get(key);
        if (v != null) return v;
        return this.find(key.toString());
    }

    /**
     * Adds all entries from provided map.
     * @return this
     */
    @Override
    public StringCollection<V> addAll(Map<? extends String, ? extends V> map) {
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
    public StringCollection<V> filter(Function<V, Boolean> filter) {
        StringCollection<V> newList = new StringCollection<>();
        String[] keys = this.keys();
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
    public StringCollection<V> filterKeys(Function<String, Boolean> filter) {
        StringCollection<V> newList = new StringCollection<>();
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
    @Override
    public StringCollection<V> removeThenReturnCollection(String k) {
        this.remove(k);
        return this;
    }

    /**
     * Clones this collection and returns new collection.
     * @see HashMap#clone()
     * @return New collection
     */
    @Override
    public StringCollection<V> clone() {
        StringCollection<V> newList = new StringCollection<>();
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
    @Override
    public <T> Collection<String, T> cast(Class<T> newType) {
        Collection<String, T> collection = new Collection<>();
        this.forEach((k, v) -> collection.add(k, newType.cast(v)));
        return collection;
    }

    /**
     * Returns all values that matches with V.
     * @return new collection
     */
    @Override
    public Collection<String, V> values(V v) {
        return new Collection<>(this.filter(f -> f.equals(v)));
    }
}
