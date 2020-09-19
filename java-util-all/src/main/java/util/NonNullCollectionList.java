package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NonNullCollectionList<V> extends CollectionList<V> {
    private static final long serialVersionUID = 11_27L;

    public NonNullCollectionList() { super(); }

    public NonNullCollectionList(List<? extends V> list) {
        super(list);
    }

    @SafeVarargs
    public NonNullCollectionList(V... v) {
        super(v);
    }

    public NonNullCollectionList(java.util.Collection<? extends V> list) {
        super(list);
    }

    @Override
    public void add(int index, V element) {
        if (element == null) return;
        super.add(index, element);
    }

    @Override
    public boolean add(V v) {
        if (v == null) return true;
        return super.add(v);
    }

    @Override
    public @NotNull CollectionList<V> unique() {
        return super.unique().nonNull();
    }

    /**
     * The <b>CollectionList.of()</b> method creates a new
     * NonNullCollectionList instance from a variable number of
     * arguments, regardless of number or type of the arguments.
     * @param t Elements of which to create the array.
     * @return A new CollectionList instance.
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    public static <T> NonNullCollectionList<T> of(T... t) { return new NonNullCollectionList<>(t); }
}
