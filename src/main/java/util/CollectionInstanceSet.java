package util;

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

    @SuppressWarnings("unchecked")
    public V first() {
        return (V) this.toArray()[0];
    }

    @SuppressWarnings("unchecked")
    public V[] valuesArray() {
        return (V[]) this.toArray();
    }

    /**
     * @param action it passes value, index.
     */
    public void foreach(BiConsumer<V, Integer> action) {
        final int[] index = {0};
        this.forEach(v -> {
            action.accept(v, index[0]);
            index[0]++;
        });
    }

    public V put(V v) {
        this.add(v);
        return v;
    }

    public boolean add(V v) {
        super.filter(v1 -> {
            try {
                return instanceOf(v1, v.getClass().getName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }).forEach(this::remove);
        return super.add(v);
    }

    public <ListLike extends List<? extends V>> void putAll(ListLike list) {
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
    public CollectionInstanceSet<V> filter(Function<V, Boolean> filter) {
        CollectionInstanceSet<V> newList = new CollectionInstanceSet<>();
        this.foreach((v, i) -> {
            if (filter.apply(v)) newList.add(v);
        });
        return newList;
    }

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
}
