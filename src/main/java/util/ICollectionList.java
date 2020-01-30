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
    void foreach(BiBiConsumer<V, Integer, ICollectionList<V>> action);
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
     * that item will be returned without using the separator.<br>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/join">https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/join</a>
     * @param s Specifies a string to separate each pair of
     *          adjacent elements of the array. The separator
     *          is converted to a string if necessary. If omitted,
     *          the array elements are separated with a comma (",").
     *          If separator is an empty string, all elements are
     *          joined without any characters in between them.
     * @return The string conversions of all array elements are joined into one string.<br>
     * <b>If an element is null or an empty array [], it is converted to an empty string.</b>
     * @see CollectionList#join()
     */
    String join(String s);

    /**
     * The <b>join()</b> method creates
     * and returns a new string by
     * concatenating all of the elements in
     * an array (or an array-like object),
     * separated by commas or a specified separator string.
     * If the array has only one item, then
     * that item will be returned without using the separator.<br>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/join">https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/join</a>
     * @return The string conversions of all array elements are joined into one string.<br>
     * <b>If an element is null or an empty array [], it is converted to an empty string.</b>
     * @see CollectionList#join(String)
     */
    String join();

    /**
     * The <b>shift()</b> method removes the first element from
     * an array and returns that removed element.
     * This method changes the length of the array.<br>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/shift">https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/shift</a>
     * @return The removed element from the array; null if the array is empty.
     */
    V shift();

    int length();

    /**
     * The <b>unshift()</b> method adds one or more elements
     * to the beginning of an array and returns the new length of the array.
     * <pre>arr.unshift(element1[, ...[, elementN]])</pre><br>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/unshift">https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/unshift</a>
     * @param v The elements to add to the front of the <i>arr</i>.
     * @return The new size of the object upon which the method was called.
     */
    @SuppressWarnings("unchecked")
    int unshift(V... v);

    /**
     * The concat() method is used to merge two or more arrays.
     * This method does not change the existing arrays,
     * but instead returns a new array.<br>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/concat">https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/concat</a>
     * @param lists Arrays and/or values to concatenate into
     *              a new array. If all valueN parameters are omitted,
     *              concat returns a shallow copy of the existing
     *              array on which it is called.
     *              See the description at MDN for more details.
     * @return A new CollectionList instance.
     */
    @SuppressWarnings("unchecked")
    CollectionList<V> concat(CollectionList<V>... lists);

    List<V> toList();

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
     * The <b>ICollectionList.ArrayOf()</b> method creates a new
     * CollectionList instance from a variable number of
     * arguments, regardless of number or type of the arguments.
     * @param t Elements of which to create the array.
     * @return A new CollectionList instance.
     */
    @SafeVarargs
    static <T> CollectionList<T> ArrayOf(T... t) {
        return new CollectionList<>(t);
    }
}
