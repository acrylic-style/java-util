package util;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class StringCollection<V> extends Collection<String, V> implements ICollection<String, V>, DeepCloneable {
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
    public V add(String key, V value) {
        return super.put(key, value);
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
    public StringCollection<V> clone() {
        StringCollection<V> newList = new StringCollection<>();
        newList.addAll(this);
        return newList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull StringCollection<V> deepClone() {
        StringCollection<V> collection = new StringCollection<>();
        this.clone().forEach((k, v) -> collection.add((String) DeepCloneable.clone(k), (V) DeepCloneable.clone(v)));
        return collection;
    }
}
