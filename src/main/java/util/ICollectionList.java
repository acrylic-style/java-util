package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ICollectionList<V> extends List<V> {
    /**
     * Adds entry into list but it returns list so it can be chained.
     * @param v Value
     * @return This list.
     */
    @NotNull
    @Contract("!null -> this")
    ICollectionList<V> addChain(@NotNull V v);

    /**
     * Returns first value of list.
     * @return First value of list. Null if size is 0.
     */
    @Nullable
    V first();

    /**
     * Returns values as array.
     * @return Values as array.
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    V[] valuesArray();

    /**
     * Returns last value of list.
     * @return Last value of list. Null if size is 0.
     */
    @Nullable
    V last();

    /**
     * Foreach all values.
     * @param action Passes Value and Index.
     */
    void foreach(@NotNull BiConsumer<V, Integer> action);

    /**
     * Foreach all values.
     * @param action Passes Value, Index and cloned list.
     */
    void foreach(@NotNull util.BiBiConsumer<V, Integer, ICollectionList<V>> action);

    /**
     * Put value into list.
     * @param v Value
     * @return Returns added value
     */
    @NotNull
    @Contract("!null -> param1")
    V put(@NotNull V v);

    /**
     * Returns flipped list.<br />
     * This method does not modify current list, it just returns new flipped list.
     * @return Flipped list
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    CollectionList<V> reverse();

    /**
     * Shuffles all entries in list.
     * @return shuffled new list
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    CollectionList<V> shuffle();

    /**
     * Add all values from list into this list.
     * @param list Another list
     * @param <ListLike> Another list type
     */
    <ListLike extends List<? extends V>> void putAll(@NotNull ListLike list);

    /**
     * Add all values from list into this list.
     * @param list Another list
     * @return This list, so it can be chained.
     */
    @NotNull
    @Contract("_ -> this")
    CollectionList<V> addAll(@Nullable CollectionList<V> list);

    /**
     * Add all values from list into this list.
     * @param list Another list
     * @return This list, so it can be chained.
     */
    @NotNull
    @Contract("_ -> this")
    CollectionList<V> putAll(@Nullable CollectionList<V> list);

    /**
     * Filters values. If returned true, that value will be kept.
     * @param filter filter function.
     * @return New filtered list
     */
    @NotNull
    @Contract(value = "!null -> new", pure = true)
    CollectionList<V> filter(@NotNull Function<V, Boolean> filter);

    /**
     * Filters values. If returned true, that value will be kept.
     * @param filter filter function.
     * @return New filtered list if not empty, null otherwise.
     */
    @Nullable
    CollectionList<V> filterNullable(@NotNull Function<V, Boolean> filter);

    /**
     * Creates shallow copy of this list.
     * @return Shallow copy of this list.
     */
    @NotNull
    @Contract("-> new")
    CollectionList<V> clone();

    /**
     * Remove then return collection.
     * @param v Value
     * @return This list, so it can be chained.
     */
    @NotNull
    @Contract("!null -> this")
    CollectionList<V> removeThenReturnCollection(@NotNull V v);

    /**
     * The <b>map()</b> method <b>creates a new array</b> populated with the results of calling a provided function on every element in the calling array.
     * @param function Function that will run to create a new array.
     * @return New array with new type.
     */
    @NotNull
    @Contract(value = "!null -> new", pure = true)
    <T> CollectionList<T> map(@NotNull Function<V, T> function);

    /**
     * The <b>map()</b> method <b>creates a new array</b> populated with the results of calling a provided function on every element in the calling array.
     * @param function Function that will run to create a new array.
     * @return New array with new type.
     */
    @NotNull
    @Contract(value = "!null -> new", pure = true)
    <T> CollectionList<T> map(@NotNull BiFunction<V, Integer, T> function);

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
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    String join(@Nullable String s);

    /**
     * The <b>join()</b> method creates and returns a new list by concatenating all of the elements in an array,
     * separated by V. If the array has only one item, then that item will be returned without using the V.
     * @param v The object
     * @return New list
     */
    @NotNull
    @Contract(value = "!null -> new", pure = true)
    CollectionList<V> joinObject(@NotNull V v);

    /**
     * Simply creates new list with same type and return it.
     * @return New list with the same type
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    CollectionList<V> newList();

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
    @NotNull
    @Contract(value = "-> new", pure = true)
    String join();

    /**
     * The <b>shift()</b> method removes the first element from
     * an array and returns that removed element.
     * This method changes the length of the array.<br>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/shift">https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/shift</a>
     * @return The removed element from the array; null if the array is empty.
     */
    @Nullable
    V shift();

    /**
     * Returns length(size) of this list.
     * @return Size of this list
     */
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
    int unshift(@Nullable V... v);

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
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    CollectionList<V> concat(@Nullable CollectionList<V>... lists);

    /**
     * Returns unique list. This method does not modify
     * the original list, and returns new list.
     * @return New list that contains only unique values
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    CollectionList<V> unique();

    /**
     * Just returns list.
     * @return Returns java.util.List so it can be used to something you want to use list for any reason you specify.
     */
    @NotNull
    @Contract("-> this")
    List<V> toList();

    /**
     * Converts list into map.
     * @param function Function that will be used to generate map.
     * @param <A> Key type
     * @param <B> Value type
     * @return New collection
     */
    @NotNull
    @Contract(value = "!null -> new", pure = true)
    <A, B> Collection<A, B> toMap(@NotNull Function<V, Map.Entry<A, B>> function);

    /**
     * Creates list from map values.
     * @param map Map
     * @param <T> Type that list will create with
     * @return New list
     */
    @NotNull
    static <T> CollectionList<T> fromValues(@NotNull Map<?, ? extends T> map) {
        return new CollectionList<>(map.values());
    }

    /**
     * Creates list from map keys.
     * @param map Map
     * @param <T> Type that list will create with
     * @return New list
     */
    @NotNull
    static <T> CollectionList<T> fromKeys(@NotNull Map<? extends T, ?> map) {
        return new CollectionList<>(map.keySet());
    }

    /**
     * Wrap list with CollectionList.
     * @return New list
     */
    @NotNull
    static <T> CollectionList<T> asList(@NotNull List<? extends T> list) {
        return new CollectionList<>(list);
    }

    /**
     * Turn array into list.
     * @param list Array
     * @return New list
     */
    @NotNull
    static <T> CollectionList<T> asList(@NotNull T[] list) {
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
    @NotNull
    static <T> CollectionList<T> ArrayOf(@NotNull T... t) {
        return new CollectionList<>(t);
    }
}
