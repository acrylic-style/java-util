package util;

import org.jetbrains.annotations.NotNull;

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
     * {@inheritDoc}
     */
    @Override
    public V first() {
        return this.valuesList().first();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String firstKey() {
        return this.keysList().first();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V last() { return this.valuesList().last(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public String lastKey() { return this.keysList().last(); }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String[] keys() {
        final Object[] a = this.keySet().toArray();
        List<Object> keysObj = Arrays.asList(a);
        CollectionList<String> keys = new CollectionList<>();
        keysObj.forEach(k -> keys.add((String) k));
        return keys.valuesArray();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public CollectionList<String> keysList() {
        return new CollectionList<>(this.keySet());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public V[] valuesArray() {
        return (V[]) this.values().toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionList<V> valuesList() {
        return new CollectionList<>(this.values());
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public StringCollection<V> addAll(@NotNull Map<? extends String, ? extends V> map) {
        super.putAll(map);
        return this;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public StringCollection<V> removeThenReturnCollection(String k) {
        this.remove(k);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringCollection<V> clone() {
        StringCollection<V> newList = new StringCollection<>();
        newList.addAll(this);
        return newList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String, V> values(V v) {
        return new Collection<>(this.filter(f -> f.equals(v)));
    }
}
