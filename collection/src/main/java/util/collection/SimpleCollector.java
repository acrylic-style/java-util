package util.collection;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.reflect.ReflectionHelper;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class SimpleCollector<T, A, R> implements Collector<T, A, R> {
    public static final Set<Characteristics> CH_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
    private final Supplier<A> supplier;
    private final BiConsumer<A, T> accumulator;
    private final BinaryOperator<A> combiner;
    private final Function<A, R> finisher;
    private final Set<Characteristics> characteristics;

    public SimpleCollector(@NotNull Supplier<A> supplier,
                           @NotNull BiConsumer<A, T> accumulator,
                           @NotNull BinaryOperator<A> combiner,
                           @NotNull Function<A, R> finisher,
                           @NotNull Set<Characteristics> characteristics) {
        this.supplier = supplier;
        this.accumulator = accumulator;
        this.combiner = combiner;
        this.finisher = finisher;
        this.characteristics = characteristics;
    }

    @SuppressWarnings("unchecked")
    public SimpleCollector(@NotNull Supplier<A> supplier,
                           @NotNull BiConsumer<A, T> accumulator,
                           @NotNull BinaryOperator<A> combiner,
                           @NotNull Set<Characteristics> characteristics) {
        this(supplier, accumulator, combiner, a -> (R) a, characteristics);
    }

    @Override
    public Supplier<A> supplier() { return supplier; }

    @Override
    public BiConsumer<A, T> accumulator() { return accumulator; }

    @Override
    public BinaryOperator<A> combiner() { return combiner; }

    @Override
    public Function<A, R> finisher() { return finisher; }

    @Override
    public Set<Characteristics> characteristics() { return characteristics; }

    @SuppressWarnings({ "unchecked" })
    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static <T, U extends List<T>> Collector<T, ?, U> of(@NotNull Class<?> clazz) {
        return new SimpleCollector<>(
                () -> (U) ReflectionHelper.createInstance(clazz),
                List::add,
                (left, right) -> { left.addAll(right); return left; },
                CH_ID
        );
    }
}
