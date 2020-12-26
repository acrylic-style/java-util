package util;

import org.jetbrains.annotations.NotNull;
import util.magic.Magic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @deprecated i have no idea why do we have this
 */
@Deprecated
public class CollectionInstanceSet<V> extends CollectionList<V> implements Serializable {
    private static final long serialVersionUID = Magic.VERSION;

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

    public boolean add(V v) {
        super.filter(v1 -> v.getClass().isInstance(v1)).forEach(this::remove);
        return super.add(v);
    }

    @Override
    public @NotNull CollectionInstanceSet<V> newList() {
        checkClass("#newList", CollectionInstanceSet.class);
        return new CollectionInstanceSet<>();
    }

    @Override
    public @NotNull CollectionInstanceSet<V> newList(Collection<? extends V> list) {
        checkClass("#newList(java.util.Collection)", CollectionInstanceSet.class);
        return new CollectionInstanceSet<>();
    }

    @Override
    public @NotNull <E> CollectionInstanceSet<E> createList() {
        checkClass("#createList", CollectionInstanceSet.class);
        return new CollectionInstanceSet<>();
    }
}
