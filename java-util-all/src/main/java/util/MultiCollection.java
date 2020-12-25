package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public class MultiCollection<K, V> implements DeepCloneable {
    private final Collection<K, CollectionList<?, V>> map = new Collection<>();

    @Contract("_, _ -> param2")
    public V add(@NotNull K key, @NotNull V value) {
        map.add(key, getOrDefault(key, new CollectionList<>()).thenAdd(value));
        return value;
    }

    @NotNull
    public CollectionList<?, V> getOrDefault(@NotNull K key, @NotNull CollectionList<?, V> values) {
        return map.getOrDefault(key, values);
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return map.size();
    }

    public int size(K k) {
        return map.getOrDefault(k, new CollectionList<>()).size();
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.  More formally, returns <tt>true</tt> if and only if
     * this map contains a mapping for a key <tt>k</tt> such that
     * <tt>(key==null ? k==null : key.equals(k))</tt>.  (There can be
     * at most one such mapping.)
     *
     * @param key key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key
     * @throws ClassCastException   if the key is of an inappropriate type for
     *                              this map
     *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified key is null and this map
     *                              does not permit null keys
     *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    public boolean containsKey(@NotNull K key) {
        return map.containsKey(key);
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.  More formally, returns <tt>true</tt> if and only if
     * this map contains at least one mapping to a value <tt>v</tt> such that
     * <tt>(value==null ? v==null : value.equals(v))</tt>.  This operation
     * will probably require time linear in the map size for most
     * implementations of the <tt>Map</tt> interface.
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     * specified value
     * @throws ClassCastException   if the value is of an inappropriate type for
     *                              this map
     *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified value is null and this
     *                              map does not permit null values
     *                              (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    public boolean containsValue(@Nullable V value) {
        AtomicBoolean found = new AtomicBoolean();
        map.forEach((k, values) -> {
            if (values.contains(value)) found.set(true);
        });
        return found.get();
    }

    public boolean containsEntry(@NotNull K k, @Nullable V v) {
        return getOrDefault(k, new CollectionList<>()).contains(v);
    }

    @Contract("_, _ -> param2")
    @NotNull
    public V put(@NotNull K k, @NotNull V v) {
        return add(k, v);
    }

    /**
     * Get an entire list.
     * @param key Key
     * @return List, null if not found
     */
    public CollectionList<?, V> get(K key) {
        return map.get(key);
    }

    /**
     * Get value by index.
     * @param key Key
     * @param i Index
     * @return Value, null if {@link IndexOutOfBoundsException} occurs.
     */
    public V get(@NotNull K key, int i) {
        try {
            return getOrDefault(key, new CollectionList<>()).get(i);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Removes a value from list.
     * @param k Key
     * @param v Value
     */
    public boolean remove(@NotNull K k, @Nullable V v) {
        return getOrDefault(k, new CollectionList<>()).remove(v);
    }

    /**
     * Removes a value from list.
     * @throws IndexOutOfBoundsException When index is out of bounds
     * @return Removed value
     */
    public V remove(@NotNull K k, int i) {
        return getOrDefault(k, new CollectionList<>()).remove(i);
    }

    public void putAll(@NotNull K k, Iterable<? extends V> iterable) {
        CollectionList<?, V> list = getOrDefault(k, new CollectionList<>());
        iterable.forEach(list::add);
        map.add(k, list);
    }

    public void putAll(MultiCollection<? extends K, V> map) {
        map.forEach(this.map::add);
    }

    public MultiCollection<K, V> concat(MultiCollection<? extends K, V> map) {
        putAll(map);
        return this;
    }

    public void forEach(BiConsumer<K, CollectionList<?, V>> consumer) {
        map.forEach(consumer);
    }

    public void foreach(BiConsumer<K, V> consumer) {
        map.forEach((k, v) -> v.forEach(v2 -> consumer.accept(k, v2)));
    }

    /**
     * Removes all entries from list.
     * @param k Key
     * @return Removed list
     */
    public CollectionList<?, V> removeAll(@NotNull K k) {
        return map.remove(k);
    }

    /**
     * Removes all of the mappings from this map (optional operation).
     * The map will be empty after this call returns.
     */
    public void clear() {
        map.clear();
    }

    public void clear(K k) {
        map.getOrDefault(k, new CollectionList<>()).clear();
    }

    public boolean isEmpty(K k) {
        return map.getOrDefault(k, new CollectionList<>()).isEmpty();
    }

    @NotNull
    public Collection<K, CollectionList<?, V>> getMap() {
        return map;
    }

    public void addAll(MultiCollection<? extends K, V> collection) {
        putAll(collection);
    }

    @NotNull
    @Contract("-> new")
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public MultiCollection<K, V> clone() {
        MultiCollection<K, V> collection = new MultiCollection<>();
        collection.addAll(this);
        return collection;
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     *
     * @return a set view of the keys contained in this map
     */
    @NotNull
    public Set<K> keySet() {
        return map.keySet();
    }

    @NotNull
    public CollectionList<?, K> keysList() {
        return map.keysList();
    }

    @NotNull
    public K[] keys() {
        return map.keys();
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a collection view of the values contained in this map
     */
    @NotNull
    public CollectionList<?, CollectionList<?, V>> values() {
        return new CollectionList<>(map.values());
    }

    @NotNull
    @Contract("-> new")
    public CollectionList<?, Map.Entry<K, CollectionList<?, V>>> entries() {
        CollectionList<?, Map.Entry<K, CollectionList<?, V>>> entries = new CollectionList<>();
        map.forEach((k, v) -> entries.add(new AbstractMap.SimpleEntry<>(k, v)));
        return entries;
    }

    @NotNull
    public Collection<K, CollectionList<?, V>> asMap() {
        return map;
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation, or through the
     * <tt>setValue</tt> operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations.  It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map
     */
    @NotNull
    public Set<Map.Entry<K, CollectionList<?, V>>> entrySet() {
        return new HashSet<>(entries());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public @NotNull Object deepClone() {
        Collection<K, V> collection = new Collection<>();
        map.clone().forEach((k, v) -> collection.add((K) DeepCloneable.clone(k), (V) DeepCloneable.clone(v)));
        return collection;
    }
}
