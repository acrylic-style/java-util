package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public interface ICollection<K, V> extends Map<K, V>, DeepCloneable {
    /**
     * Returns first value of the collection.
     * @return First value of the collection
     */
    @Nullable
    default V first() { return valuesList().first(); }

    /**
     * Returns first key of the collection.
     * @return First key of the collection
     */
    @Nullable
    default K firstKey() { return keysList().first(); }

    /**
     * Returns last value of the collection.
     * @return Last value of the collection
     */
    @Nullable
    default V last() { return valuesList().last(); }

    /**
     * Returns last key of the collection.
     * @return Last key of the collection
     */
    @Nullable
    default K lastKey() { return keysList().last(); }

    /**
     * Returns keys as array.
     * @return Keys as array
     */
    @SuppressWarnings("unchecked")
    @NotNull
    default K[] keys() { return ICollectionList.asList(this.keySet().toArray()).map(o -> (K) o).valuesArray(); }

    /**
     * Returns keys as list.
     * @return Keys as list
     */
    @SuppressWarnings("unchecked")
    @NotNull
    @Contract("-> new")
    default <C extends CollectionList<C, K>> C keysList() { return (C) new CollectionList<>(this.keySet()); }

    /**
     * Returns values as array.
     * @return Values as array
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    default V[] valuesArray() { return this.valuesList().valuesArray(); }

    /**
     * Returns values as list.
     * @return Values as list
     */
    default <C extends CollectionList<C, V>> C valuesList() { return (C) newList(this.values()); }

    /**
     * Foreach all values in collection.
     * @param action Passes Value and index.
     */
    default void foreach(BiConsumer<V, Integer> action) {
        final int[] index = {0};
        this.values().forEach(v -> {
            action.accept(v, index[0]);
            index[0]++;
        });
    }

    /**
     * Foreach all values in collection.
     * @param action Passes Value, index and collection.
     */
    default void foreach(BiBiConsumer<V, Integer, ICollection<K, V>> action) {
        final int[] index = {0};
        this.values().forEach(v -> {
            action.accept(v, index[0], this);
            index[0]++;
        });
    }

    /**
     * Foreach all keys in collection.
     * @param action Passes key and index.
     */
    default void foreachKeys(BiConsumer<K, Integer> action) {
        final int[] index = {0};
        this.keySet().forEach(k -> {
            action.accept(k, index[0]);
            index[0]++;
        });
    }

    /**
     * Foreach all keys in collection.
     * @param action Passes key, index and collection.
     */
    default void foreachKeys(BiBiConsumer<K, Integer, ICollection<K, V>> action) {
        final int[] index = {0};
        this.keySet().forEach(k -> {
            action.accept(k, index[0], this);
            index[0]++;
        });
    }

    /**
     * Foreach all keys/values in collection.
     * @param action Passes key, value and collection.
     */
    default void forEach(BiBiConsumer<K, V, ICollection<K, V>> action) {
        this.forEach((k, v) -> action.accept(k, v, this));
    }

    /**
     * Foreach all keys/values in collection.
     * @param action Passes key, value, index and collection.
     */
    default void forEach(BiBiBiConsumer<K, V, Integer, ICollection<K, V>> action) {
        final int[] index = {0};
        this.forEach((k, v) -> action.accept(k, v, index[0]++, this));
    }

    default V add(K key, V value) { return this.put(key, value); }

    /**
     * Add all entries into collection then return this collection.
     * @param map Map that you want to add all entries into this list
     * @return This list, so it can be chained
     */
    @Contract("_ -> this")
    default ICollection<K, V> addAll(@NotNull Map<? extends K, ? extends V> map) {
        this.putAll(map);
        return this;
    }

    /**
     * Filters value, return true to add(keep) into collection.<br />
     * This method does not modify this collection.
     * @param filter Filter function. Return true to keep, return false to remove from collection.
     * @return Filtered collection
     */
    default Collection<K, V> filter(Function<V, Boolean> filter) {
        Collection<K, V> newList = new Collection<>();
        K[] keys = this.keys();
        this.foreach((v, i) -> {
            if (filter.apply(v)) newList.put(keys[i], v);
        });
        return newList.clone();
    }

    /**
     * Filters key, return true to add(keep) into collection.<br />
     * This method does not modify this collection.
     * @param filter Filter function. Return true to keep, return false to remove from collection.
     * @return Filtered collection
     */
    default ICollection<K, V> filterKeys(Function<K, Boolean> filter) {
        Collection<K, V> newList = new Collection<>();
        V[] values = this.valuesArray();
        this.foreachKeys((k, i) -> {
            if (filter.apply(k)) newList.put(k, values[i]);
        });
        return newList;
    }

    /**
     * Remove key then return collection.
     * @param k Key that removes from collection.
     * @return This collection
     * @deprecated use {@link #thenRemove(Object)}
     */
    @Deprecated
    @NotNull
    default ICollection<K, V> removeThenReturnCollection(K k) {
        this.remove(k);
        return this;
    }

    @NotNull
    default ICollection<K, V> thenAdd(@NotNull K k, @Nullable V v) {
        this.add(k, v);
        return this;
    }

    @NotNull
    default ICollection<K, V> thenRemove(@NotNull K k) {
        this.remove(k);
        return this;
    }

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
    default ICollection<K, V> values(V v) { return this.filter(f -> f.equals(v)); }

    /**
     * Converts this collection into new collection (type).
     * @param keyFunction Key function for convert to another key type.
     * @param valueFunction Value function for convert to another value type.
     * @param <A> Key type
     * @param <B> Value type
     * @return New collection with new types.
     */
    default <A, B> ICollection<A, B> map(BiFunction<K, V, A> keyFunction, BiFunction<K, V, B> valueFunction) {
        Collection<A, B> newCollection = new Collection<>();
        this.forEach((k, v) -> {
            A a = keyFunction.apply(k, v);
            B b = valueFunction.apply(k, v);
            newCollection.add(a, b);
        });
        return newCollection;
    }

    /**
     * Converts this collection into new collection (type).
     * @param valueFunction Value function for convert to another value type.
     * @param <B> Value type
     * @return New collection with new types.
     */
    default <B> ICollection<K, B> mapValues(BiFunction<K, V, B> valueFunction) {
        return map((k, v) -> k, valueFunction);
    }

    /**
     * Converts this collection into new collection (type).
     * @param keyFunction Key function for convert to another value type.
     * @param <A> Value type
     * @return New collection with new types.
     */
    default <A> ICollection<A, V> mapKeys(BiFunction<K, V, A> keyFunction) {
        return map(keyFunction, (k, v) -> v);
    }

    /**
     * Converts map into entry list.
     * @return List of entries.
     */
    @NotNull
    default CollectionList<?, Entry<K, V>> toEntryList() {
        CollectionList<?, Entry<K, V>> entries = new CollectionList<>();
        this.forEach((k, v) -> entries.add(new HashMap.SimpleEntry<>(k, v)));
        return entries;
    }

    /**
     * Converts map into map list.
     * @return List of maps.
     */
    @NotNull
    default CollectionList<?, Map<K, V>> toMapList() {
        CollectionList<?, Map<K, V>> entries = new CollectionList<>();
        this.forEach((k, v) -> entries.add(Collections.singletonMap(k, v)));
        return entries;
    }

    @NotNull
    <S> CollectionList<?, S> toList(BiFunction<K, V, S> function);
    
    default CollectionList<?, V> newList(java.util.Collection<V> collection) {
        return new CollectionList<>(collection);
    }

    default boolean mayContainsKey(@NotNull K key) { return find(key) != null; }

    @Nullable
    default V find(@NotNull K key) {
        Validate.notNull(key, "key cannot be null");
        return this.filterKeys(k -> k.equals(key)).valuesList().first();
    }

    @Nullable
    default V find(@NotNull Predicate<? super K> predicate) {
        Validate.notNull(predicate, "predicate cannot be null");
        return this.filterKeys(predicate::test).first();
    }

    @Nullable
    default Entry<K, V> findEntry(@NotNull Predicate<? super K> predicate) {
        Validate.notNull(predicate, "predicate cannot be null");
        return this.filterKeys(predicate::test).toEntryList().first();
    }

    static <K, V> @NotNull Collection<K, V> asCollection(Map<? extends K, ? extends V> map) {
        Collection<K, V> collection = new Collection<>();
        collection.addAll(map);
        return collection;
    }

    /**
     * @deprecated high maintenance cost
     */
    @Deprecated
    static <K, V> Collection<K, V> asCollectionSync(Map<? extends K, ? extends V> map) {
        CollectionSync<K, V> collection = new CollectionSync<>();
        collection.addAll(map);
        return collection;
    }

    /**
     * @deprecated high maintenance cost
     */
    @Deprecated
    static <K, V> Collection<K, V> asCollectionStrictSync(Map<? extends K, ? extends V> map) {
        CollectionStrictSync<K, V> collection = new CollectionStrictSync<>();
        collection.addAll(map);
        return collection;
    }

    static <C extends CollectionList<C, Entry<K, V>>, K, V extends Comparable<? super V>> Collection<K, V> sortByValue(Collection<K, V> map) {
        C list = (C) map.toEntryList();
        list.sort(Entry.comparingByValue());
        Collection<K, V> result = new Collection<>();
        for (Entry<K, V> entry : list) result.put(entry.getKey(), entry.getValue());
        return result;
    }

    static <C extends CollectionList<C, Entry<K, V>>, K extends Comparable<? super K>, V> Collection<K, V> sortByKey(Collection<K, V> map) {
        C list = (C) map.toEntryList();
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
