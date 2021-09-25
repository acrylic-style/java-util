package util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * The Great HashMap
 * @see HashMap The old HashMap (impl)
 * @see Map The old map (interface)
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
    @Override
    @SuppressWarnings("unchecked")
    public Collection<K, V> clone() {
        return (Collection<K, V>) super.clone();
    }

    @Override
    @NotNull
    public <S> CollectionList<S> toList(BiFunction<K, V, S> function) {
        CollectionList<S> list = new CollectionList<>();
        this.forEach((k, v) -> list.add(function.apply(k, v)));
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public Collection<K, V> deepClone() {
        Collection<K, V> collection = new Collection<>();
        this.clone().forEach((k, v) -> collection.add((K) DeepCloneable.clone(k), (V) DeepCloneable.clone(v)));
        return collection;
    }
}
