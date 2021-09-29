package util.collection;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.magic.Magic;

import java.io.Serializable;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This CollectionList provides a better feature than
 * just a List or ArrayList, like using #map without
 * using the Stream. Additionally, this list provides
 * the some useful methods. This list is based on the
 * {@link java.util.ArrayList ArrayList} and you may not
 * get the performance advantages by using this list.
 * This list just provides the useful methods.
 */
public class CollectionList<V> extends AbstractArrayCollectionList<V> implements ICollectionList<V>, Cloneable, Serializable {
    private static final long serialVersionUID = Magic.VERSION;

    public CollectionList() {
        super();
    }

    public CollectionList(@NotNull List<? extends V> list) {
        super(list);
    }

    @SafeVarargs
    public CollectionList(@NotNull V... v) {
        super(v);
    }

    public CollectionList(@NotNull java.util.Collection<? extends V> list) {
        super(list);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    @Contract(value = "-> new", pure = true)
    public CollectionList<V> newList() {
        checkClass("#newList", CollectionList.class);
        return new CollectionList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull CollectionList<V> newList(java.util.Collection<? extends V> list) {
        checkClass("#newList(java.util.collection.Collection)", CollectionList.class);
        return new CollectionList<>(list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull <E> CollectionList<E> createList() {
        checkClass("#createList", CollectionList.class);
        return new CollectionList<>();
    }
    
    protected final void checkClass(String what, Class<?> clazz) {
        if (!this.getClass().equals(clazz))
            throw new ClassCastException(this.getClass().getCanonicalName() + " must implement " + what);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CollectionList)) return false;
        CollectionList<?> list = (CollectionList<?>) o;
        return super.equals(list);
    }

    @Override
    public @NotNull <T> CollectionList<T> map(@NotNull Function<V, T> function) {
        return (CollectionList<T>) super.map(function);
    }

    @Override
    public @NotNull <T> CollectionList<T> map(@NotNull BiFunction<V, Integer, T> function) {
        return (CollectionList<T>) super.map(function);
    }

    @Override
    public @NotNull <T> CollectionList<T> flatMap(@NotNull Function<V, ? extends List<? extends T>> function) {
        return (CollectionList<T>) ICollectionList.super.flatMap(function);
    }

    @Override
    public @NotNull <T> CollectionList<T> arrayFlatMap(@NotNull Function<V, T[]> function) {
        return (CollectionList<T>) ICollectionList.super.arrayFlatMap(function);
    }

    /**
     * The <b>CollectionList.of()</b> method creates a new
     * CollectionList instance from a variable number of
     * arguments, regardless of number or type of the arguments.
     * @param t Elements of which to create the array.
     * @return A new CollectionList instance.
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    @SafeVarargs
    public static <T> CollectionList<T> of(T... t) { return new CollectionList<>(t); }
}