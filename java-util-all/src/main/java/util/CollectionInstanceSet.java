package util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CollectionInstanceSet<V> extends CollectionList<V> {
    public CollectionInstanceSet() {
        super();
    }

    public CollectionInstanceSet(ArrayList<? extends V> list) {
        super();
        this.addAll(list);
    }

    public CollectionInstanceSet(java.util.Collection<? extends V> list) {
        super();
        this.addAll(list);
    }

    public CollectionInstanceSet(CollectionList<? extends V> list) {
        super();
        this.addAll(list);
    }

    public CollectionInstanceSet(CollectionInstanceSet<? extends V> list) {
        super();
        this.addAll(list);
    }

    public CollectionInstanceSet(Set<? extends V> list) {
        super();
        this.addAll(list);
    }

    public V first() { return this.valuesArray()[0]; }

    @NotNull
    public V[] valuesArray() { return super.valuesArray(); }

    // @Nullable
    // public V last() { return this.valuesArray()[0]; }

    /**
     * @param action it passes value, index.
     */
    public void foreach(@NotNull BiConsumer<V, Integer> action) {
        final int[] index = {0};
        this.forEach(v -> {
            action.accept(v, index[0]);
            index[0]++;
        });
    }

    @NotNull
    public V put(@NotNull V v) {
        this.add(v);
        return v;
    }

    public boolean add(V v) {
        super.filter(v1 -> {
            try {
                return instanceOf(v1, v.getClass().getName());
            } catch (ClassNotFoundException e) {
                return false;
            }
        }).forEach(this::remove);
        return super.add(v);
    }

    public <ListLike extends List<? extends V>> void putAll(@NotNull ListLike list) {
        list.forEach(this::add);
    }

    public <ListLike extends List<? extends V>> void addAll(ListLike list) {
        list.forEach(this::add);
    }

    public CollectionInstanceSet<V> putAll(CollectionInstanceSet<V> list) {
        this.addAll(list);
        return this;
    }

    /**
     * Filters values. If returned true, that value will be kept. Returns new Collection of filtered values.
     * @param filter filter function.
     */
    @NotNull
    public CollectionInstanceSet<V> filter(@NotNull Function<V, Boolean> filter) {
        CollectionInstanceSet<V> newList = new CollectionInstanceSet<>();
        this.foreach((v, i) -> {
            if (filter.apply(v)) newList.add(v);
        });
        return newList;
    }

    @NotNull
    public CollectionInstanceSet<V> clone() {
        CollectionInstanceSet<V> newList = new CollectionInstanceSet<>();
        newList.addAll(this);
        return newList;
    }

    public CollectionInstanceSet<V> removeReturnCollection(V v) {
        this.remove(v);
        return this;
    }

    public static <T> CollectionInstanceSet<T> fromValues(HashMap<?, ? extends T> map) {
        return new CollectionInstanceSet<>(map.values());
    }

    public static <T> CollectionInstanceSet<T> fromKeys(HashMap<? extends T, ?> map) {
        return new CollectionInstanceSet<>(map.keySet());
    }

    private boolean instanceOf(Object obj, String clazz) throws ClassNotFoundException {
        return Class.forName(clazz).isInstance(obj);
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public CollectionInstanceSet<V> deepClone() {
        CollectionInstanceSet<V> set = new CollectionInstanceSet<>();
        this.clone().forEach(v -> set.add((V) DeepCloneable.clone(v)));
        return set;
    }
}
