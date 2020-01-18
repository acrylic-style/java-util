package util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ICollectionList<V> extends List<V> {
    V first();
    V[] valuesArray();
    V last();
    void foreach(BiConsumer<V, Integer> action);
    V put(V v);
    CollectionList<V> reverse();

    /**
     * Shuffles all entries in list.
     * @return shuffled new list
     */
    CollectionList<V> shuffle();
    <ListLike extends List<? extends V>> void putAll(ListLike list);
    CollectionList<V> addAll(CollectionList<V> list);
    CollectionList<V> putAll(CollectionList<V> list);
    /**
     * Filters values. If returned true, that value will be kept.
     * @param filter filter function.
     * @return New filtered list
     */
    CollectionList<V> filter(Function<V, Boolean> filter);
    /**
     * Filters values. If returned true, that value will be kept.
     * @param filter filter function.
     * @return New filtered list if not empty, null otherwise.
     */
    CollectionList<V> filterNullable(Function<V, Boolean> filter);
    CollectionList<V> clone();
    CollectionList<V> removeThenReturnCollection(V v);

    /**
     * The <b>map()</b> method <b>creates a new array</b> populated with the results of calling a provided function on every element in the calling array.
     * @param function Function that will run to create a new array.
     * @return New array with new type.
     */
    <T> CollectionList<T> map(Function<V, T> function);

    /**
     * The <b>map()</b> method <b>creates a new array</b> populated with the results of calling a provided function on every element in the calling array.
     * @param function Function that will run to create a new array.
     * @return New array with new type.
     */
    <T> CollectionList<T> map(BiFunction<V, Integer, T> function);

    /**
     * The <b>join()</b> method creates
     * and returns a new string by
     * concatenating all of the elements in
     * an array (or an array-like object),
     * separated by commas or a specified separator string.
     * If the array has only one item, then
     * that item will be returned without using the separator.
     * @param s Specifies a string to separate each pair of
     *          adjacent elements of the array. The separator
     *          is converted to a string if necessary. If omitted,
     *          the array elements are separated with a comma (",").
     *          If separator is an empty string, all elements are
     *          joined without any characters in between them.
     * @return The string conversions of all array elements are joined into one string.<br>
     * <b>If an element is null or an empty array [], it is converted to an empty string.</b>
     */
    String join(String s);

    static <T> CollectionList<T> fromValues(Map<?, ? extends T> map) {
        return new CollectionList<>(map.values());
    }

    static <T> CollectionList<T> fromKeys(Map<? extends T, ?> map) {
        return new CollectionList<>(map.keySet());
    }

    static <T> CollectionList<T> asList(List<? extends T> list) {
        return new CollectionList<>(list);
    }

    static <T> CollectionList<T> asList(T[] list) {
        CollectionList<T> collectionList = new CollectionList<>();
        collectionList.addAll(Arrays.asList(list));
        return collectionList;
    }

    /**
     * Casts type to another. Exactly same method as CollectionList#cast().
     * @param <T> New value type, if it was impossible to cast, ClassCastException will be thrown.
     * @return New collection
     * @throws ClassCastException Thrown when impossible to cast
     */
    static <T> CollectionList<T> cast(CollectionList<?> l, Class<T> t) {
        CollectionList<T> list = new CollectionList<>();
        l.forEach(v -> list.add(t.cast(v)));
        return list;
    }


}
