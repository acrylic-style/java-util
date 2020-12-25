package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import util.collection.SimpleCollector;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

@SuppressWarnings("unchecked")
public interface ICollectionList<C extends ICollectionList<C, V>, V> extends List<V>, DeepCloneable {
    @Override
    default boolean isEmpty() { return size() == 0; }

    /**
     * Returns the distribution of value.<br />
     * The implementation should use {@link Object#equals(Object)} to compare between objects.
     * @return the distribution, returned in 0 - 1 range.
     * @see #distributionEntry(Object)
     */
    @Range(from = 0, to = 1)
    default double distribution(@NotNull V v) {
        return filter(v2 -> v2.equals(v)).size() / (double) size();
    }

    /**
     * Returns the distribution of value.<br />
     * The implementation should use {@link Object#equals(Object)} to compare between objects.
     * @return key is the distribution, returned in 0 - 1 range. the value is the how many values found.
     * @see #distribution(Object)
     */
    default Map.Entry<Double, Integer> distributionEntry(@NotNull V v) {
        int size = filter(v2 -> v2.equals(v)).size();
        return new AbstractMap.SimpleImmutableEntry<>(size / (double) size(), size);
    }

    /**
     * Adds entry into list but it returns list so it can be chained.
     * @param v Value
     * @return This list.
     * @deprecated use {@link #thenAdd(Object)}
     */
    @NotNull
    @Contract("_ -> this")
    @Deprecated
    default C addChain(@NotNull V v) { return this.thenAdd(v); }

    /**
     * Returns first value of list.
     * @return First value of list. Null if size is 0.
     */
    @Nullable
    default V first() { return this.length() == 0 ? null : this.valuesArray()[0]; }

    /**
     * Returns values as array.
     * @return Values as array.
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    default V[] valuesArray() { return (V[]) this.toArray(); }

    /**
     * Returns last value of list.
     * @return Last value of list. Null if size is 0.
     */
    @Nullable
    default V last() { return this.length() == 0 ? null : this.valuesArray()[this.length()-1]; }

    /**
     * Foreach all values.
     * @param action Passes Value and Index.
     */
    default void foreach(@NotNull BiConsumer<V, Integer> action) {
        final int[] index = {0};
        this.forEach(v -> {
            action.accept(v, index[0]);
            index[0]++;
        });
    }

    /**
     * Foreach all values.
     * @param action Passes Value, Index and cloned list.
     */
    default void foreach(@NotNull util.BiBiConsumer<V, Integer, ICollectionList<?, V>> action) {
        final int[] index = {0};
        this.forEach(v -> {
            action.accept(v, index[0], this.clone());
            index[0]++;
        });
    }

    /**
     * Foreach all values, but with two values.
     * @param action the consumer to run. second argument may be null.
     */
    default void biForEach(@NotNull BiConsumer<@NotNull V, @Nullable V> action) {
        foreach((v, i) -> {
            if (i % 2 == 1) return;
            action.accept(v, getNullable(i + 1));
        });
    }

    @Nullable
    default V getNullable(int index) { return has(index) ? get(index) : null; }

    default boolean has(int index) { return this.size() > index; }

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
    default C reverse() {
        C target = this.clone();
        Collections.reverse(target);
        return target;
    }

    /**
     * Shuffles all entries in list.
     * @return shuffled new list
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    default C shuffle() {
        C target = this.clone();
        Collections.shuffle(target);
        return target;
    }

    /**
     * Add all values from list into this list.
     * @param list Another list
     * @param <ListLike> Another list type
     */
    @SuppressWarnings("UseBulkOperation")
    default <ListLike extends List<? extends V>> void putAll(@NotNull ListLike list) {
        list.forEach(this::add);
    }

    /**
     * Add all values from list into this list.
     * @param list Another list
     * @return This list, so it can be chained.
     */
    @NotNull
    @Contract("_ -> this")
    default C addAll(@Nullable ICollectionList<?, V> list) {
        if (list != null) {
            list.forEach(this::add);
        }
        return (C) this;
    }

    @NotNull
    default C addAll(@Nullable V[] array) {
        if (array != null) addAll(Arrays.asList(array));
        return (C) this;
    }

    /**
     * @deprecated use {@link #thenAddAllIterable(Iterable)}
     */
    @Deprecated
    default C addAllChain(@Nullable Iterable<V> iterable) {
        if (iterable != null) {
            iterable.forEach(this::add);
        }
        return (C) this;
    }

    /**
     * Add all values from list into this list.
     * @param list Another list
     * @return This list, so it can be chained.
     */
    @NotNull
    @Contract("_ -> this")
    default C putAll(@Nullable ICollectionList<?, V> list) { return this.addAll(list); }

    /**
     * Filters values. If returned true, that value will be kept.
     * @param filter filter function.
     * @return New filtered list
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    default C filter(@NotNull Function<V, Boolean> filter) {
        C newList = newList();
        this.forEach(v -> {
            if (filter.apply(v)) newList.add(v);
        });
        return newList;
    }

    /**
     * Filters values. If returned true, that value will be kept.
     * @param filter filter function.
     * @return New filtered list
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    default C filter(@NotNull BiPredicate<V, Integer> filter) {
        C newList = newList();
        this.foreach((v, i) -> {
            if (filter.test(v, i)) newList.add(v);
        });
        return newList;
    }

    /**
     * Filters values. If returned true, that value will be kept.
     * @param filter filter function.
     * @return New filtered list if not empty, null otherwise.
     */
    @Nullable
    default C filterNullable(@NotNull Function<V, Boolean> filter) {
        C newList = newList();
        this.foreach((v, i) -> {
            if (filter.apply(v)) newList.add(v);
        });
        return newList.size() == 0 ? null : newList;
    }

    /**
     * Creates shallow copy of this list.
     * @return Shallow copy of this list.
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    C clone();

    /**
     * Remove then return collection.
     * @param v Value
     * @return This list, so it can be chained.
     * @deprecated use {@link #thenRemove(Object)}
     */
    @NotNull
    @Contract("_ -> this")
    @Deprecated
    default C removeThenReturnCollection(@NotNull V v) { return thenRemove(v); }

    /**
     * The <b>map()</b> method <b>creates a new array</b> populated with the results of calling a provided function on every element in the calling array.
     * @param function Function that will run to create a new array.
     * @return New array with new type.
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    default <T> ICollectionList<?, T> map(@NotNull Function<V, T> function) {
        ICollectionList<?, T> newList = createList();
        this.forEach(v -> newList.add(function.apply(v)));
        return newList;
    }

    @NotNull
    @Contract(pure = true)
    default <T> ICollectionList<?, T> flatMap(@NotNull Function<V, ? extends List<? extends T>> function) {
        ICollectionList<?, T> newList = createList();
        this.forEach(v -> newList.addAll(function.apply(v)));
        return newList;
    }

    @NotNull
    @Contract(pure = true)
    default <T> ICollectionList<?, T> arrayFlatMap(@NotNull Function<V, T[]> function) {
        ICollectionList<?, T> newList = createList();
        this.forEach(v -> newList.addAll(function.apply(v)));
        return newList;
    }

    @NotNull
    default C thenRemove(@NotNull Supplier<? extends V> supplier) {
        this.remove(supplier.get());
        return (C) this;
    }

    @NotNull
    default C thenRemove(@NotNull V v) {
        this.remove(v);
        return (C) this;
    }

    @NotNull
    default C then(@NotNull Consumer<? super V> action) {
        forEach(action);
        return (C) this;
    }

    @NotNull
    default C then(@NotNull BiConsumer<V, Integer> action) {
        foreach(action);
        return (C) this;
    }

    @NotNull
    default C then(@NotNull BiBiConsumer<V, Integer, ICollectionList<?, V>> action) {
        foreach(action);
        return (C) this;
    }

    @NotNull
    default C thenAdd(@NotNull Supplier<? extends V> supplier) {
        this.add(supplier.get());
        return (C) this;
    }

    @NotNull
    default C thenAdd(@NotNull V v) {
        this.add(v);
        return (C) this;
    }

    @NotNull
    default C thenAddAll(@NotNull Supplier<? extends List<? extends V>> supplier) {
        this.addAll(supplier.get());
        return (C) this;
    }

    @NotNull
    default C thenAddAll(@NotNull List<? extends V> list) {
        this.addAll(list);
        return (C) this;
    }

    @NotNull
    default C thenAddAllIterable(@Nullable Iterable<V> iterable) {
        if (iterable != null) {
            iterable.forEach(this::add);
        }
        return (C) this;
    }

    @NotNull
    default C limit(long max) {
        return clone().filter((v, i) -> i > max);
    }

    /**
     * The <b>map()</b> method <b>creates a new array</b> populated with the results of calling a provided function on every element in the calling array.
     * @param function Function that will run to create a new array.
     * @return New array with new type.
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    default <T> ICollectionList<?, T> map(@NotNull BiFunction<V, Integer, T> function) {
        ICollectionList<?, T> newList = createList();
        final int[] index = {0};
        this.forEach(v -> {
            newList.add(function.apply(v, index[0]));
            index[0]++;
        });
        return newList;
    }

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
    default String join(@Nullable String s) {
        if (this.isEmpty()) return "";
        StringBuilder str = new StringBuilder();
        this.foreach((a, i) -> {
            if (i != 0) str.append(s == null ? "," : s);
            str.append(a);
        });
        return str.toString();
    }

    /**
     * The <b>join()</b> method creates and returns a new list by concatenating all of the elements in an array,
     * separated by V. If the array has only one item, then that item will be returned without using the V.
     * @param v The object
     * @return New list
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    default C joinObject(@NotNull V v) {
        if (this.isEmpty()) return this.clone();
        C list = this.newList();
        this.foreach((a, i) -> {
            if (i != 0) list.add(v);
            list.add(a);
        });
        return list;
    }

    /**
     * Simply creates new list with same type and return it.
     * @return New list with the same type
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    C newList();

    /**
     * Simply creates new list with same type and return it.
     * @return New list with the same type
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    C newList(@Nullable java.util.Collection<? extends V> list);

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
    <E> ICollectionList<?, E> createList();

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
    default String join() { return join(null); }

    /**
     * The <b>shift()</b> method removes the first element from
     * an array and returns that removed element.
     * This method changes the length of the array.<br>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/shift">https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/shift</a>
     * @return The removed element from the array; null if the array is empty.
     */
    @Nullable
    default V shift() {
        if (this.isEmpty()) return null;
        return this.remove(0);
    }

    /**
     * The <b>shift()</b> method removes the first element from
     * an array and returns the list.
     * This method changes the length of the array.<br>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/shift">https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/shift</a>
     * @return The list
     * @deprecated Use {@link #thenShift()}, it has exactly same effect.
     */
    @NotNull
    @Deprecated
    default C shiftChain() { return thenShift(); }

    /**
     * The <b>shift()</b> method removes the first element from
     * an array and returns the list.
     * This method changes the length of the array.<br>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/shift">https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/shift</a>
     * @return The list
     */
    @NotNull
    default C thenShift() {
        shift();
        return (C) this;
    }

    /**
     * Returns length(size) of this list.
     * @return Size of this list
     */
    default int length() { return this.size(); }

    /**
     * The <b>unshift()</b> method adds one or more elements
     * to the beginning of an array and returns the new length of the array.
     * <pre>arr.unshift(element1[, ...[, elementN]])</pre><br>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/unshift">https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/unshift</a>
     * @param v The elements to add to the front of the <i>arr</i>.
     * @return The new size of the object upon which the method was called.
     */
    @SuppressWarnings("unchecked")
    default int unshift(@Nullable V... v) {
        if (v == null || v.length == 0) return this.size();
        for (int i = 0; i < v.length; i++) this.add(i, v[i]);
        return this.size();
    }

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

    @NotNull
    @Override
    default ListIterator<V> listIterator() { return this.toList().listIterator(); }

    @NotNull
    @Override
    default Iterator<V> iterator() { return this.toList().iterator(); }

    @Override
    default Spliterator<V> spliterator() { return this.toList().spliterator(); }

    @NotNull
    @Override
    default ListIterator<V> listIterator(int index) { return this.toList().listIterator(index); }

    @NotNull
    @Override
    default C subList(int fromIndex, int toIndex) { return newList(this.toList().subList(fromIndex, toIndex)); }

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
    default C concat(@Nullable ICollectionList<?, V>... lists) {
        if (lists == null) return this.clone();
        C list = newList();
        list.addAll(this);
        for (ICollectionList<?, V> vs : lists) list.addAll(vs);
        return list.unique();
    }

    /**
     * Returns unique list. This method does not modify
     * the original list, and returns new list.
     * @return New list that contains only unique values
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    default C unique() { return newList(new HashSet<>(this.clone())); }

    /**
     * Returns non-null list. This method does not modify
     * the original list, and returns new list.
     * @return New list that contains only non-null values
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    default C nonNull() { return this.clone().filter(Objects::nonNull); }

    /**
     * Just returns list.
     * @return Returns java.util.List so it can be used to something you want to use list for any reason you specify.
     */
    @NotNull
    @Contract("-> this")
    default List<V> toList() { return this; }

    /**
     * Converts list into map.
     * @param function Function that will be used to generate map.
     * @param <A> Key type
     * @param <B> Value type
     * @return New collection
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    default <A, B> ICollection<A, B> toMap(@NotNull Function<V, Map.Entry<A, B>> function) {
        util.Collection<A, B> collection = new util.Collection<>();
        this.unique().forEach(v -> {
            Map.Entry<A, B> entry = function.apply(v);
            collection.add(entry.getKey(), entry.getValue());
        });
        return collection;
    }

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
    default <A, B> ICollection<A, B> toMap(@NotNull Function<V, A> function1, @NotNull Function<V, B> function2) {
        util.Collection<A, B> collection = new util.Collection<>();
        this.unique().forEach(v -> collection.add(function1.apply(v), function2.apply(v)));
        return collection;
    }

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
    default C slice(int start) {
        C list = newList();
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
    default C slice(int start, int end) {
        C list = newList();
        foreach((v, i) -> {
            if (i >= start && i < end) list.add(v);
        });
        return list;
    }

    /**
     * Simple redirection to the {@link List#contains(Object)}.
     */
    default boolean includes(Object o) { return contains(o); }

    @NotNull
    default C sorted(Comparator<? super V> comparator) {
        this.sort(comparator);
        return (C) this;
    }

    /**
     * Sorts the list.
     * @throws ClassCastException when the class does not extends Comparable
     * @return the sorted list
     */
    @SuppressWarnings("unchecked")
    @NotNull
    default C sorted() {
        return this.sorted((Comparator<V>) Comparator.naturalOrder());
    }

    /**
     * Converts list into byte array.
     * The element must be number.
     * @return byte array
     */
    @Contract(pure = true)
    default byte@NotNull[] toByteArray() {
        return toByteArray((CollectionList<?, ? extends Number>) this);
    }

    /**
     * Converts list into int array.
     * The element must be number.
     * @return int array
     */
    @Contract(pure = true)
    default int@NotNull[] toIntArray() {
        return toIntArray((CollectionList<?, ? extends Number>) this);
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    default C deepClone() {
        C set = newList();
        this.clone().forEach(v -> set.add((V) DeepCloneable.clone(v)));
        return set;
    }

    // ===== Static Methods

    @Contract(pure = true)
    static byte@NotNull[] toByteArray(@NotNull List<? extends Number> list) {
        byte[] bytes = new byte[list.size()];
        AtomicInteger i = new AtomicInteger();
        list.forEach(number -> bytes[i.getAndIncrement()] = number.byteValue());
        return bytes;
    }

    @Contract(pure = true)
    static int@NotNull[] toIntArray(@NotNull List<? extends Number> list) {
        int[] bytes = new int[list.size()];
        AtomicInteger i = new AtomicInteger();
        list.forEach(number -> bytes[i.getAndIncrement()] = number.intValue());
        return bytes;
    }

    /* Static methods */

    /**
     * Creates list from keys from map.
     * @param map Map
     * @param <T> Type that list will create with
     * @return New list
     */
    @Contract("_ -> new")
    @NotNull
    static <C extends CollectionList<C, T>, T> C fromKeys(@NotNull Map<? extends T, ?> map) {
        return (C) new CollectionList<>(map.keySet());
    }

    /**
     * Creates list from values from map.
     * @param map Map
     * @param <T> Type that list will create with
     * @return New list
     */
    @Contract("_ -> new")
    @NotNull
    static <C extends CollectionList<C, T>, T> C fromValues(@NotNull Map<?, ? extends T> map) {
        return (C) new CollectionList<>(map.values());
    }

    /**
     * Wrap list with CollectionList.
     * @return New list
     */
    @Contract("_ -> new")
    @NotNull
    static <C extends CollectionList<C, T>, T> C asList(@NotNull List<? extends T> list) {
        return (C) new CollectionList<>(list);
    }

    /**
     * Turn array into list.
     * @param list Array
     * @return New list
     */
    @NotNull
    static <C extends CollectionList<C, T>, T> C asList(@NotNull T[] list) {
        C collectionList = (C) new CollectionList<>();
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
    static <C extends CollectionList<C, T>, T> C of(@NotNull T... t) {
        return (C) new CollectionList<>(t);
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

    Set<Collector.Characteristics> CH_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));

    @Contract(value = " -> new", pure = true)
    @NotNull
    static <T> Collector<T, ?, CollectionList<?, T>> toCollectionList() {
        return SimpleCollector.of(CollectionList.class);
    }

    @Contract(value = " -> new", pure = true)
    @NotNull
    static <T> Collector<T, ?, CollectionSet<?, T>> toCollectionSet() {
        return SimpleCollector.of(CollectionSet.class);
    }

    @NotNull
    static Object[] toArray(@NotNull List<?> list) {
        final Object[] objects = new Object[list.size()];
        AtomicInteger index = new AtomicInteger();
        list.forEach(obj -> objects[index.getAndIncrement()] = obj);
        return objects;
    }
}
