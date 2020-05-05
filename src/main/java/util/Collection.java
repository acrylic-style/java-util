package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
     * Constructs an empty Collection with the specified initial capacity and the default load factor (0.75).
     */
    public Collection(int size) {
        super(size);
    }

    /**
     * Constructs an empty Collection with the specified initial capacity and the specified load factor.
     */
    public Collection(int size, int loadFactor) {
        super(size, loadFactor);
    }

    /**
     * Constructs a collection and add all entries in the specified list.
     * @param entries Entry list
     */
    public Collection(@NotNull java.util.Collection<Entry<? extends K, ? extends V>> entries) {
        super();
        entries.forEach(entry -> this.add(entry.getKey(), entry.getValue()));
    }

    /**
     * Constructs this Collection with values.
     * @param map will be added with this constructor
     */
    public Collection(@NotNull Map<? extends K, ? extends V> map) {
        super();
        this.addAll(map);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    public V first() {
        return this.valuesList().first();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    public K firstKey() {
        return this.keysList().first();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    public V last() { return this.valuesList().last(); }

    /**
     * {@inheritDoc}
     */
    @Nullable
    public K lastKey() { return this.keysList().last(); }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public K[] keys() {
        return ICollectionList.asList(this.keySet().toArray()).map(o -> (K) o).valuesArray();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Contract("-> new")
    public CollectionList<K> keysList() {
        return new CollectionList<>(this.keySet());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    public V[] valuesArray() {
        return this.valuesList().valuesArray();
    }

    /**
     * {@inheritDoc}
     */
    public CollectionList<V> valuesList() {
        return new CollectionList<>(this.values());
    }

    /**
     * {@inheritDoc}
     */
    public void foreach(BiConsumer<V, Integer> action) {
        final int[] index = {0};
        this.values().forEach(v -> {
            action.accept(v, index[0]);
            index[0]++;
        });
    }

    /**
     * {@inheritDoc}
     */
    public void foreach(BiBiConsumer<V, Integer, ICollection<K, V>> action) {
        final int[] index = {0};
        this.values().forEach(v -> {
            action.accept(v, index[0], this);
            index[0]++;
        });
    }

    /**
     * {@inheritDoc}
     */
    public void foreachKeys(BiConsumer<K, Integer> action) {
        final int[] index = {0};
        this.keySet().forEach(k -> {
            action.accept(k, index[0]);
            index[0]++;
        });
    }

    /**
     * {@inheritDoc}
     */
    public void foreachKeys(BiBiConsumer<K, Integer, ICollection<K, V>> action) {
        final int[] index = {0};
        this.keySet().forEach(k -> {
            action.accept(k, index[0], this);
            index[0]++;
        });
    }

    /**
     * {@inheritDoc}
     */
    public void forEach(BiBiConsumer<K, V, ICollection<K, V>> action) {
        this.forEach((k, v) -> action.accept(k, v, this));
    }

    /**
     * {@inheritDoc}
     */
    public void forEach(BiBiBiConsumer<K, V, Integer, ICollection<K, V>> action) {
        final int[] index = {0};
        this.forEach((k, v) -> action.accept(k, v, index[0]++, this));
    }

    /**
     * {@inheritDoc}
     */
    public V add(K key, V value) {
        return super.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Contract("!null -> this")
    public Collection<K, V> addAll(@NotNull Map<? extends K, ? extends V> map) {
        super.putAll(map);
        return this;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public Collection<K, V> filterKeys(Function<K, Boolean> filter) {
        Collection<K, V> newList = new Collection<>();
        V[] values = this.valuesArray();
        this.foreachKeys((k, i) -> {
            if (filter.apply(k)) newList.put(k, values[i]);
        });
        return newList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<K, V> removeThenReturnCollection(K k) {
        this.remove(k);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Collection<K, V> clone() {
        return (Collection<K, V>) super.clone();
    }

    /**
     * {@inheritDoc}
     * @deprecated Use {@link Collection#map(BiFunction, BiFunction)} instead.
     */
    @Override
    @Deprecated
    public <T> Collection<K, T> cast(Class<T> newType) {
        Collection<K, T> collection = new Collection<>();
        this.forEach((k, v) -> collection.add(k, newType.cast(v)));
        return collection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<K, V> values(V v) {
        return new Collection<>(this.filter(f -> f.equals(v)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <A, B> Collection<A, B> map(BiFunction<K, V, A> keyFunction, BiFunction<K, V, B> valueFunction) {
        Collection<A, B> newCollection = new Collection<>();
        this.forEach((k, v) -> {
            A a = keyFunction.apply(k, v);
            B b = valueFunction.apply(k, v);
            newCollection.add(a, b);
        });
        return newCollection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionList<Entry<K, V>> toEntryList() {
        CollectionList<Entry<K, V>> entries = new CollectionList<>();
        this.forEach((k, v) -> entries.add(new HashMap.SimpleEntry<>(k, v)));
        return entries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionList<Map<K, V>> toMapList() {
        CollectionList<Map<K, V>> entries = new CollectionList<>();
        this.forEach((k, v) -> entries.add(Collections.singletonMap(k, v)));
        return entries;
    }

    @Override
    public <S> CollectionList<S> toList(BiFunction<K, V, S> function) {
        CollectionList<S> list = new CollectionList<>();
        this.forEach((k, v) -> list.add(function.apply(k, v)));
        return list;
    }
}
