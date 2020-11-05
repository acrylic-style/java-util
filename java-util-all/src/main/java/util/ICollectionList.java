package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ICollectionList<V> extends List<V>, DeepCloneable {
    /**
     * Returns the distribution of value.<br />
     * The implementation should use {@link Object#equals(Object)} to compare between objects.
     * @return the distribution, returned in 0 - 1 range.
     * @see #distributionEntry(Object)
     */
    @Range(from = 0, to = 1)
    double distribution(@NotNull V v);

    /**
     * Returns the distribution of value.<br />
     * The implementation should use {@link Object#equals(Object)} to compare between objects.
     * @return key is the distribution, returned in 0 - 1 range. the value is the how many values found.
     * @see #distribution(Object)
     */
    Map.Entry<Double, Integer> distributionEntry(@NotNull V v);

    /**
     * Adds entry into list but it returns list so it can be chained.
     * @param v Value
     * @return This list.
     */
    @NotNull
    @Contract("_ -> this")
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
     * Foreach all values, but with two values.
     * @param action the consumer to run. second argument (U) may be null if size is odd.
     */
    default void biForEach(@NotNull BiConsumer<V, V> action) {
        foreach((v, i) -> {
            if (i % 2 == 1) return;
            action.accept(v, getNullable(i + 1));
        });
    }

    @Nullable
    default V getNullable(int index) {
        return has(index) ? get(index) : null;
    }

    default boolean has(int index) {
        return this.size() > index;
    }

    /**
     * Put value into list.
     * @param v Value
     * @return Returns added value
     */
    @NotNull
    @Contract("_ -> param1")
    V put(@NotNull V v);

    /**
     * Returns flipped list.<br />
     * This method does not modify current list, it just returns new flipped list.
     * @return Flipped list
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    ICollectionList<V> reverse();

    /**
     * Shuffles all entries in list.
     * @return shuffled new list
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    ICollectionList<V> shuffle();

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
    ICollectionList<V> addAll(@Nullable ICollectionList<V> list);

    default ICollectionList<V> addAll(@Nullable V[] array) {
        if (array != null) addAll(Arrays.asList(array));
        return this;
    }

    default ICollectionList<V> addAllChain(@Nullable Iterable<V> iterable) {
        if (iterable != null) {
            iterable.forEach(this::add);
        }
        return this;
    }

    /**
     * Add all values from list into this list.
     * @param list Another list
     * @return This list, so it can be chained.
     */
    @NotNull
    @Contract("_ -> this")
    ICollectionList<V> putAll(@Nullable ICollectionList<V> list);

    /**
     * Filters values. If returned true, that value will be kept.
     * @param filter filter function.
     * @return New filtered list
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    ICollectionList<V> filter(@NotNull Function<V, Boolean> filter);

    /**
     * Filters values. If returned true, that value will be kept.
     * @param filter filter function.
     * @return New filtered list if not empty, null otherwise.
     */
    @Nullable
    ICollectionList<V> filterNullable(@NotNull Function<V, Boolean> filter);

    /**
     * Creates shallow copy of this list.
     * @return Shallow copy of this list.
     */
    @NotNull
    @Contract("-> new")
    ICollectionList<V> clone();

    /**
     * Remove then return collection.
     * @param v Value
     * @return This list, so it can be chained.
     */
    @NotNull
    @Contract("_ -> this")
    ICollectionList<V> removeThenReturnCollection(@NotNull V v);

    /**
     * The <b>map()</b> method <b>creates a new array</b> populated with the results of calling a provided function on every element in the calling array.
     * @param function Function that will run to create a new array.
     * @return New array with new type.
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    <T> ICollectionList<T> map(@NotNull Function<V, T> function);

    @NotNull
    @Contract(pure = true)
    default <T> ICollectionList<T> flatMap(@NotNull Function<V, ? extends List<? extends T>> function) {
        ICollectionList<T> newList = createList();
        this.forEach(v -> newList.addAll(function.apply(v)));
        return newList;
    }

    @NotNull
    @Contract(pure = true)
    default <T> ICollectionList<T> arrayFlatMap(@NotNull Function<V, T[]> function) {
        ICollectionList<T> newList = createList();
        this.forEach(v -> newList.addAll(function.apply(v)));
        return newList;
    }

    /**
     * The <b>map()</b> method <b>creates a new array</b> populated with the results of calling a provided function on every element in the calling array.
     * @param function Function that will run to create a new array.
     * @return New array with new type.
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    <T> ICollectionList<T> map(@NotNull BiFunction<V, Integer, T> function);

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
    @Contract(value = "_ -> new", pure = true)
    ICollectionList<V> joinObject(@NotNull V v);

    /**
     * Simply creates new list with same type and return it.
     * @return New list with the same type
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    ICollectionList<V> newList();

    /**
     * Simply creates new list with same type and return it.
     * @return New list with the same type
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    ICollectionList<V> newList(@Nullable java.util.Collection<? extends V> list);

    /**
     * Simply creates new list with different type and return it.
     * If the list cannot implement this method for any reason, the class must override the following methods:
     * <ul>
     *     <li>N/A</li>
     * </ul>
     * @return New list with the different type
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    <E> ICollectionList<E> createList();

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
     * The <b>shift()</b> method removes the first element from
     * an array and returns the list.
     * This method changes the length of the array.<br>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/shift">https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/shift</a>
     * @return The list
     */
    @NotNull
    ICollectionList<V> shiftChain();

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
     * Returns whether any elements of this list match the provided
     * predicate.  May not evaluate the predicate on all elements if not
     * necessary for determining the result.  If the list is empty then
     * {@code false} is returned and the predicate is not evaluated.
     *
     * <p>This is a <a href="package-summary.html#StreamOps">short-circuiting
     * terminal operation</a>.
     *
     * @apiNote
     * This method evaluates the <em>existential quantification</em> of the
     * predicate over the elements of the list (for some x P(x)).
     *
     * @param predicate a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                  <a href="package-summary.html#Statelessness">stateless</a>
     *                  predicate to apply to elements of this stream
     * @return {@code true} if any elements of the list match the provided
     * predicate, otherwise {@code false}
     */
    default boolean anyMatch(Predicate<V> predicate) {
        if (isEmpty()) return false;
        return filterNullable(predicate::test) != null;
    }

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
    ICollectionList<V> concat(@Nullable ICollectionList<V>... lists);

    /**
     * Returns unique list. This method does not modify
     * the original list, and returns new list.
     * @return New list that contains only unique values
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    ICollectionList<V> unique();

    /**
     * Returns non-null list. This method does not modify
     * the original list, and returns new list.
     * @return New list that contains only non-null values
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    ICollectionList<V> nonNull();

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
    @Contract(value = "_ -> new", pure = true)
    <A, B> ICollection<A, B> toMap(@NotNull Function<V, Map.Entry<A, B>> function);

    /**
     * Converts list into map.
     * @param function1 Function that will be used to generate key of the map.
     * @param function2 Function that will be used to generate value of the map.
     * @param <A> Key type
     * @param <B> Value type
     * @return New collection
     */
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    <A, B> ICollection<A, B> toMap(@NotNull Function<V, A> function1, @NotNull Function<V, B> function2);

    /**
     * Merges 2 entries into the one.
     * @param biFunction the function to be run, t will be the 'accumulator', the accumulated value previously returned
     *                   in the last invocation of the callback.
     * @throws ClassCastException When the result cannot be cast to T.
     * @throws RuntimeException When the list is empty
     * @param <U> the new type of the returned list
     */
    @SuppressWarnings("unchecked")
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    default <U> U reduce(@NotNull BiFunction<U, @Nullable V, U> biFunction) {
        if (this.isEmpty()) throw new RuntimeException("List is empty!");
        AtomicReference<U> ref = new AtomicReference<>((U) this.get(0));
        foreach((v, i) -> {
            if (i > 0) ref.set(biFunction.apply(ref.get(), v));
        });
        return ref.get();
    }

    /**
     * Merges 2 entries into the one.
     * @param biFunction the function to be run, t will be the 'accumulator', the accumulated value previously returned
     *                   in the last invocation of the callback.
     * @param <U> the new type of the returned list
     */
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    default <U> U reduce(@NotNull BiFunction<U, V, U> biFunction, @Nullable U initialValue) {
        AtomicReference<U> ref = new AtomicReference<>(initialValue);
        forEach((v) -> ref.set(biFunction.apply(ref.get(), v)));
        return ref.get();
    }

    /**
     * Merges 2 entries into the one.
     * @param reducer the {@link Reducer} to use
     * @param <U> the new type of the returned list
     */
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    default <U> U reduce(@NotNull Reducer<V, U> reducer, @Nullable U initialValue) { return reduce(reducer.biFunction, initialValue); }

    /**
     * Merges 2 entries into the one.
     * @param reducer the {@link Reducer} to use
     * @param <U> the new type of the returned list
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    default <U> U reduce(@NotNull Reducer<V, U> reducer) { return reduce(reducer.biFunction); }

    /**
     * The slice() method returns a shallow copy of a portion of an array into a new array object selected from start
     * to end (end not included) where start and end represent the index of items in that array. The original array will
     * not be modified. Unlike the JavaScript implementation, the start does not support the negative value.
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/slice">MDN Docs</a>
     * @param start Zero-based index at which to start extraction.
     * @return A new array containing the extracted elements.
     */
    @NotNull
    default ICollectionList<V> slice(int start) {
        ICollectionList<V> list = newList();
        foreach((v, i) -> {
            if (i >= start) list.add(v);
        });
        return list;
    }

    /**
     * The slice() method returns a shallow copy of a portion of an array into a new array object selected from start
     * to end (end not included) where start and end represent the index of items in that array. The original array will
     * not be modified. Unlike the JavaScript implementation, the start and the end does not support the negative value.
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/slice">MDN Docs</a>
     * @param start Zero-based index at which to start extraction.
     * @param end Zero-based index before which to end extraction. slice extracts up to but not including end. For
     *            example, slice(1,4) extracts the second element through the fourth element
     *            (elements indexed 1, 2, and 3).
     * @return A new array containing the extracted elements.
     */
    @NotNull
    default ICollectionList<V> slice(int start, int end) {
        ICollectionList<V> list = newList();
        foreach((v, i) -> {
            if (i >= start && i < end) list.add(v);
        });
        return list;
    }

    /**
     * Simple redirection to the {@link List#contains(Object)}.
     */
    default boolean includes(Object o) { return contains(o); }

    default ICollectionList<V> sorted(Comparator<? super V> comparator) {
        this.sort(comparator);
        return this;
    }

    /**
     * Sorts the list.
     * @throws ClassCastException when the class does not extends Comparable
     * @return the sorted list
     */
    @SuppressWarnings("unchecked")
    default ICollectionList<V> sorted() {
        this.sorted((Comparator<V>) Comparator.naturalOrder());
        return this;
    }

    /* Static methods */

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
        collectionList.addAll(list);
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
    static <T> CollectionList<T> of(@NotNull T... t) {
        return new CollectionList<>(t);
    }

    final class Reducer<T, U> {
        public static final Reducer<Integer, Integer> SUM_INTEGER = new Reducer<>(Integer::sum);
        public static final Reducer<Double, Double> SUM_DOUBLE = new Reducer<>(Double::sum);
        public static final Reducer<Long, Long> SUM_LONG = new Reducer<>(Long::sum);
        public static final Reducer<Float, Float> SUM_FLOAT = new Reducer<>(Float::sum);
        public static final Reducer<Byte, Byte> SUM_BYTE = new Reducer<>((b1, b2) -> (byte) (b1 + b2));
        public static final Reducer<Short, Short> SUM_SHORT = new Reducer<>((s1, s2) -> (short) (s1 + s2));
        public static final Reducer<String, String> CONCAT_STRING = new Reducer<>((s1, s2) -> s1 + s2);

        @NotNull public final BiFunction<U, T, U> biFunction;

        private Reducer(@NotNull BiFunction<U, T, U> biFunction) {
            Validate.notNull(biFunction, "BiFunction cannot be null");
            this.biFunction = biFunction;
        }
    }
}
