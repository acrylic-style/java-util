package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ICollection<K, V> extends Map<K, V>, DeepCloneable {
    /**
     * Returns first value of the collection.
     * @return First value of the collection
     */
    @Nullable
    V first();

    /**
     * Returns first key of the collection.
     * @return First key of the collection
     */
    @Nullable
    K firstKey();

    /**
     * Returns last value of the collection.
     * @return Last value of the collection
     */
    @Nullable
    V last();

    /**
     * Returns last key of the collection.
     * @return Last key of the collection
     */
    @Nullable
    K lastKey();

    /**
     * Returns keys as array.
     * @return Keys as array
     */
    @NotNull
    K[] keys();

    /**
     * Returns keys as list.
     * @return Keys as list
     */
    @NotNull
    @Contract("-> new")
    CollectionList<K> keysList();

    /**
     * Returns values as array.
     * @return Values as array
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    V[] valuesArray();

    /**
     * Returns values as list.
     * @return Values as list
     */
    CollectionList<V> valuesList();

    /**
     * Foreach all values in collection.
     * @param action Passes Value and index.
     */
    void foreach(BiConsumer<V, Integer> action);

    /**
     * Foreach all values in collection.
     * @param action Passes Value, index and collection.
     */
    void foreach(BiBiConsumer<V, Integer, ICollection<K, V>> action);

    /**
     * Foreach all keys in collection.
     * @param action Passes key and index.
     */
    void foreachKeys(BiConsumer<K, Integer> action);

    /**
     * Foreach all keys in collection.
     * @param action Passes key, index and collection.
     */
    void foreachKeys(BiBiConsumer<K, Integer, ICollection<K, V>> action);

    /**
     * Foreach all keys/values in collection.
     * @param action Passes key, value and collection.
     */
    void forEach(BiBiConsumer<K, V, ICollection<K, V>> action);

    /**
     * Foreach all keys/values in collection.
     * @param action Passes key, value, index and collection.
     */
    void forEach(BiBiBiConsumer<K, V, Integer, ICollection<K, V>> action);

    /**
     * Add all entries into collection then return this collection.
     * @param map Map that you want to add all entries into this list
     * @return This list, so it can be chained
     */
    @Contract("!null -> this")
    Collection<K, V> addAll(@NotNull Map<? extends K, ? extends V> map);

    /**
     * Filters value, return true to add(keep) into collection.<br />
     * This method does not modify this collection.
     * @param filter Filter function. Return true to keep, return false to remove from collection.
     * @return Filtered collection
     */
    Collection<K, V> filter(Function<V, Boolean> filter);

    /**
     * Filters key, return true to add(keep) into collection.<br />
     * This method does not modify this collection.
     * @param filter Filter function. Return true to keep, return false to remove from collection.
     * @return Filtered collection
     */
    Collection<K, V> filterKeys(Function<K, Boolean> filter);

    /**
     * Remove key then return collection.
     * @param k Key that removes from collection.
     * @return This collection
     */
    Collection<K, V> removeThenReturnCollection(K k);

    /**
     * Creates shallow copy of this collection.
     * @return Shallow copy of this collection
     */
    Collection<K, V> clone();

    /**
     * Filter by value(v).
     * @param v Value
     * @return New collection
     */
    Collection<K, V> values(V v);

    /**
     * Converts this collection into new collection (type).
     * @param keyFunction Key function for convert to another key type.
     * @param valueFunction Value function for convert to another value type.
     * @param <A> Key type
     * @param <B> Value type
     * @return New collection with new types.
     */
    <A, B> Collection<A, B> map(BiFunction<K, V, A> keyFunction, BiFunction<K, V, B> valueFunction);

    /**
     * Converts this collection into new collection (type).
     * @param valueFunction Value function for convert to another value type.
     * @param <B> Value type
     * @return New collection with new types.
     */
    default <B> Collection<K, B> mapValues(BiFunction<K, V, B> valueFunction) {
        return map((k, v) -> k, valueFunction);
    }

    /**
     * Converts this collection into new collection (type).
     * @param keyFunction Key function for convert to another value type.
     * @param <A> Value type
     * @return New collection with new types.
     */
    default <A> Collection<A, V> mapKeys(BiFunction<K, V, A> keyFunction) {
        return map(keyFunction, (k, v) -> v);
    }

    /**
     * Converts map into entry list.
     * @return List of entries.
     */
    @NotNull
    CollectionList<Entry<K, V>> toEntryList();

    /**
     * Converts map into map list.
     * @return List of maps.
     */
    @NotNull
    CollectionList<Map<K, V>> toMapList();

    @NotNull
    <S> CollectionList<S> toList(BiFunction<K, V, S> function);

    default boolean mayContainsKey(@NotNull K key) { return find(key) != null; }

    @Nullable
    default V find(@NotNull K key) {
        Validate.notNull(key, "key cannot be null");
        V original = get(key); // try #get before doing expensive action
        if (original != null) return original;
        return this.filterKeys(k -> k.equals(key)).valuesList().first();
    }

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

    static <K, V extends Comparable<? super V>> Collection<K, V> sortByValue(Collection<K, V> map) {
        CollectionList<Entry<K, V>> list = map.toEntryList();
        list.sort(Entry.comparingByValue());
        Collection<K, V> result = new Collection<>();
        for (Entry<K, V> entry : list) result.put(entry.getKey(), entry.getValue());
        return result;
    }

    static <K extends Comparable<? super K>, V> Collection<K, V> sortByKey(Collection<K, V> map) {
        CollectionList<Entry<K, V>> list = map.toEntryList();
        list.sort(Entry.comparingByKey());
        Collection<K, V> result = new Collection<>();
        for (Entry<K, V> entry : list) result.put(entry.getKey(), entry.getValue());
        return result;
    }

    class NullableEntry<@Nullable K, @Nullable V> implements Map.Entry<K, V> {
        @Nullable private final K key;
        @Nullable private V value;

        public NullableEntry(@Nullable K key, @Nullable V value) {
            this.key = key;
            this.value = value;
        }

        public NullableEntry(@Nullable Map.Entry<K, V> entry) {
            this(entry == null ? null : entry.getKey(), entry == null ? null : entry.getValue());
        }

        @Nullable
        @Override
        public K getKey() { return this.key; }

        @Nullable
        @Override
        public V getValue() { return this.value; }

        @Nullable
        @Override
        public V setValue(@Nullable V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }

    class NonNullEntry<@NotNull K, @NotNull V> implements Map.Entry<K, V> {
        @NotNull private final K key;
        @NotNull private V value;

        public NonNullEntry(@NotNull K key, @NotNull V value) {
            Validate.notNull(key, "key cannot be null");
            Validate.notNull(value, "value cannot be null");
            this.key = key;
            this.value = value;
        }

        public NonNullEntry(@NotNull Map.Entry<K, V> entry) {
            this(Validate.notNull(entry.getKey(), "key cannot be null"), Validate.notNull(entry.getValue(), "value cannot be null"));
        }

        @NotNull
        @Override
        public K getKey() { return this.key; }

        @NotNull
        @Override
        public V getValue() { return this.value; }

        @NotNull
        @Override
        public V setValue(@NotNull V value) {
            Validate.notNull(value, "value cannot be null");
            V old = this.value;
            this.value = value;
            return old;
        }
    }
}
