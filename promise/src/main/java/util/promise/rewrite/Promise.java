package util.promise.rewrite;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.collection.ICollectionList;
import util.Validate;
import util.concurrent.MoreExecutorService;
import util.function.ThrowableConsumer;
import util.function.ThrowableFunction;
import util.list.*;
import util.promise.UnhandledPromiseException;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

// TODO: throw/show unhandled promise exception
public class Promise<T> {
    private MoreExecutorService executor;
    private final AtomicReference<T> value = new AtomicReference<>();
    private final AtomicReference<Throwable> error = new AtomicReference<>();
    private int current = 0;
    private int max = 0;

    private Promise(@Nullable T value, boolean pending) {
        this.executor = MoreExecutorService.of(Executors.newSingleThreadExecutor());
        this.value.set(value);
        if (!pending) {
            this.max = this.executor.submit(id -> current = id);
        }
    }

    private Promise(@Nullable Throwable throwable) {
        this.executor = MoreExecutorService.of(Executors.newSingleThreadExecutor());
        this.error.set(throwable);
        this.max = this.executor.submit(id -> {
            current = id;
            if (max == current) error.set(new UnhandledPromiseException(throwable));
        });
    }

    public Promise(@NotNull ThrowableConsumer<PromiseContext<T>> runnable) {
        this(null, runnable);
    }

    public Promise(@NotNull ThrowableFunction<PromiseContext<T>, T> supplier) {
        this(null, supplier);
    }

    private Promise(@Nullable ExecutorService executor, @NotNull ThrowableConsumer<PromiseContext<T>> consumer) {
        this(executor, consumer.toFunction());
    }

    private Promise(@Nullable ExecutorService executor, @NotNull ThrowableFunction<PromiseContext<T>, T> supplier) {
        Validate.notNull(supplier, "supplier cannot be null");
        this.executor = MoreExecutorService.of(executor != null ? executor : Executors.newSingleThreadExecutor());
        boolean converted = supplier instanceof ThrowableConsumer.ThrowableConsumerFunction;
        AtomicBoolean resolved = new AtomicBoolean(false);
        this.max = this.executor.submit(id -> {
            current = id;
            try {
                T val = supplier.run(new PromiseContext<T>() {
                    @Override
                    public void resolve(@Nullable T value) {
                        Promise.this.value.set(value);
                        resolved.set(true);
                        if (converted) {
                            synchronized (resolved) {
                                resolved.notifyAll();
                            }
                        }
                    }

                    @Override
                    public void reject(@Nullable Throwable throwable) {
                        resolved.set(true);
                        Promise.this.error.set(throwable);
                        throw Cancel.INSTANCE;
                    }
                });
                if (current == id && !converted) {
                    value.set(val);
                }
                if (converted && !resolved.get()) {
                    synchronized (resolved) {
                        while (!resolved.get()) {
                            resolved.wait();
                        }
                    }
                }
            } catch (Throwable throwable) {
                if (throwable instanceof Cancel) return;
                if (current == id) {
                    error.set(throwable);
                    if (max == current) {
                        error.set(new UnhandledPromiseException(throwable));
                    }
                }
            }
        });
    }

    @Contract(pure = true)
    @NotNull
    public static <T> Promise<T> create(@NotNull ThrowableConsumer<PromiseContext<T>> runnable) {
        return new Promise<>(runnable);
    }

    @NotNull
    public Promise<T> thenDo(@NotNull ThrowableConsumer<T> action) {
        if (error.get() != null) return setupExecutor(reject(error.get()));
        this.max = this.executor.submit(id -> {
            try {
                current = id;
                Throwable throwable = error.get();
                if (throwable != null) {
                    this.error.set(throwable);
                    throw Cancel.INSTANCE;
                }
                action.accept(this.value.get());
            } catch (Throwable throwable) {
                if (throwable instanceof Cancel) return;
                this.error.set(throwable);
                if (max == current) {
                    this.error.set(new UnhandledPromiseException(throwable));
                }
            }
        });
        return this;
    }

    @NotNull
    public Promise<T> thenDo(@NotNull Promise<T> action) {
        if (error.get() != null) return setupExecutor(reject(error.get()));
        this.max = this.executor.submit(id -> {
            try {
                current = id;
                Throwable throwable = error.get();
                if (throwable != null) {
                    this.error.set(throwable);
                    throw Cancel.INSTANCE;
                }
                action.complete();
            } catch (Throwable throwable) {
                if (throwable instanceof Cancel) return;
                this.error.set(throwable);
                if (max == current) {
                    this.error.set(new UnhandledPromiseException(throwable));
                }
            }
        });
        return this;
    }

    @NotNull
    public <R> Promise<R> then(@NotNull ThrowableFunction<T, R> function) {
        if (error.get() != null) return setupExecutor(reject(error.get()));
        // create empty promise with new type
        Promise<R> promise = setupExecutor(resolve(null, true));
        this.max = this.executor.submit(id -> {
            try {
                current = id;
                Throwable throwable = error.get();
                if (throwable != null) {
                    promise.error.set(throwable);
                    throw Cancel.INSTANCE;
                }
                R val = function.run(this.value.get());
                promise.value.set(val);
            } catch (Throwable throwable) {
                if (throwable instanceof Cancel) return;
                promise.error.set(throwable);
                if (max == current) {
                    promise.error.set(new UnhandledPromiseException(throwable));
                }
            }
        });
        return promise;
    }

    @NotNull
    public <R> Promise<R> then(@NotNull Promise<R> promiseIn) {
        if (error.get() != null) return setupExecutor(reject(error.get()));
        Promise<R> promise = setupExecutor(resolve(null, true));
        this.max = this.executor.submit(id -> {
            try {
                current = id;
                Throwable throwable = error.get();
                if (throwable != null) {
                    promise.error.set(throwable);
                    throw Cancel.INSTANCE;
                }
                R val = promiseIn.complete();
                promise.value.set(val);
            } catch (Throwable throwable) {
                if (throwable instanceof Cancel) return;
                promise.error.set(throwable);
                if (max == current) {
                    promise.error.set(new UnhandledPromiseException(throwable));
                }
            }
        });
        return promise;
    }

    @NotNull
    public <R> Promise<R> thenAsync(@NotNull Promise<Promise<R>> promiseIn) {
        if (error.get() != null) return setupExecutor(reject(error.get()));
        Promise<R> promise = setupExecutor(resolve(null, true));
        this.max = this.executor.submit(id -> {
            try {
                current = id;
                Throwable throwable = error.get();
                if (throwable != null) {
                    promise.error.set(throwable);
                    throw Cancel.INSTANCE;
                }
                R val = promiseIn.complete().complete();
                promise.value.set(val);
            } catch (Throwable throwable) {
                if (throwable instanceof Cancel) return;
                promise.error.set(throwable);
                if (max == current) {
                    promise.error.set(new UnhandledPromiseException(throwable));
                }
            }
        });
        return promise;
    }

    @NotNull
    public Promise<T> onCatch(@NotNull ThrowableConsumer<Throwable> action) {
        this.max = this.executor.submit(id -> {
            current = id;
            try {
                Throwable throwable = error.get();
                if (throwable != null) {
                    if (throwable instanceof UnhandledPromiseException) {
                        throwable = throwable.getCause();
                        error.set(throwable);
                    }
                    error.set(null);
                    action.accept(throwable);
                }
            } catch (Throwable throwable1) {
                error.set(throwable1);
                if (max == current) {
                    error.set(new UnhandledPromiseException(throwable1));
                }
            }
        });
        return this;
    }

    /**
     * Creates a promise that sleeps then returns nothing.
     * @param millis the time to sleep
     */
    @Contract("_ -> new")
    @NotNull
    public static <T> Promise<T> sleepPromise(long millis) {
        return new Promise<>(context -> {
            Thread.sleep(millis);
            context.resolve();
        });
    }

    /**
     * Creates a throwable function which sleeps for specified time and returns the provided value.
     * @param millis the time to sleep
     * @return throwable function that can be passed to {@link #then(ThrowableFunction)}
     */
    @Contract("_ -> new")
    @NotNull
    public static <T> ThrowableFunction<T, T> sleep(long millis) {
        return t -> {
            Thread.sleep(millis);
            return t;
        };
    }

    //<editor-fold defaultstate="collapsed" desc="Promise.all">
    @Contract("_ -> new")
    @NotNull
    public static <T1> L1<T1> all(
            @Nullable Promise<T1> t1
    ) {
        T1 p1 = t1 != null ? t1.complete() : null;
        return new L1<>(p1);
    }

    @SuppressWarnings("unchecked")
    @Contract("_, _ -> new")
    @NotNull
    public static <T1, T2> L2<T1, T2> all(
            @NotNull Promise<T1> t1,
            @NotNull Promise<T2> t2
    ) {
        List<?> p = Promise.allUntyped(t1, t2);
        return new L2<>((T1) p.get(0), (T2) p.get(1));
    }

    @SuppressWarnings("unchecked")
    @Contract("_, _, _ -> new")
    @NotNull
    public static <T1, T2, T3> L3<T1, T2, T3> all(
            @NotNull Promise<T1> t1,
            @NotNull Promise<T2> t2,
            @NotNull Promise<T3> t3
    ) {
        List<?> p = Promise.allUntyped(t1, t2, t3);
        return new L3<>((T1) p.get(0), (T2) p.get(1), (T3) p.get(2));
    }

    @SuppressWarnings("unchecked")
    @Contract("_, _, _, _ -> new")
    @NotNull
    public static <T1, T2, T3, T4> L4<T1, T2, T3, T4> all(
            @NotNull Promise<T1> t1,
            @NotNull Promise<T2> t2,
            @NotNull Promise<T3> t3,
            @NotNull Promise<T4> t4
    ) {
        List<?> p = Promise.allUntyped(t1, t2, t3, t4);
        return new L4<>((T1) p.get(0), (T2) p.get(1), (T3) p.get(2), (T4) p.get(3));
    }

    @SuppressWarnings("unchecked")
    @Contract("_, _, _, _, _ -> new")
    @NotNull
    public static <T1, T2, T3, T4, T5> L5<T1, T2, T3, T4, T5> all(
            @NotNull Promise<T1> t1,
            @NotNull Promise<T2> t2,
            @NotNull Promise<T3> t3,
            @NotNull Promise<T4> t4,
            @NotNull Promise<T5> t5
    ) {
        List<?> p = Promise.allUntyped(t1, t2, t3, t4, t5);
        return new L5<>((T1) p.get(0), (T2) p.get(1), (T3) p.get(2), (T4) p.get(3), (T5) p.get(4));
    }

    @SuppressWarnings("unchecked")
    @Contract("_, _, _, _, _, _ -> new")
    @NotNull
    public static <T1, T2, T3, T4, T5, T6> L6<T1, T2, T3, T4, T5, T6> all(
            @NotNull Promise<T1> t1,
            @NotNull Promise<T2> t2,
            @NotNull Promise<T3> t3,
            @NotNull Promise<T4> t4,
            @NotNull Promise<T5> t5,
            @NotNull Promise<T6> t6
    ) {
        List<?> p = Promise.allUntyped(t1, t2, t3, t4, t5, t6);
        return new L6<>((T1) p.get(0), (T2) p.get(1), (T3) p.get(2), (T4) p.get(3), (T5) p.get(4), (T6) p.get(5));
    }

    @SuppressWarnings("unchecked")
    @Contract("_, _, _, _, _, _, _ -> new")
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7> L7<T1, T2, T3, T4, T5, T6, T7> all(
            @NotNull Promise<T1> t1,
            @NotNull Promise<T2> t2,
            @NotNull Promise<T3> t3,
            @NotNull Promise<T4> t4,
            @NotNull Promise<T5> t5,
            @NotNull Promise<T6> t6,
            @NotNull Promise<T7> t7
    ) {
        List<?> p = Promise.allUntyped(t1, t2, t3, t4, t5, t6, t7);
        return new L7<>((T1) p.get(0), (T2) p.get(1), (T3) p.get(2), (T4) p.get(3), (T5) p.get(4), (T6) p.get(5), (T7) p.get(6));
    }

    @SuppressWarnings("unchecked")
    @Contract("_, _, _, _, _, _, _, _ -> new")
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8> L8<T1, T2, T3, T4, T5, T6, T7, T8> all(
            @NotNull Promise<T1> t1,
            @NotNull Promise<T2> t2,
            @NotNull Promise<T3> t3,
            @NotNull Promise<T4> t4,
            @NotNull Promise<T5> t5,
            @NotNull Promise<T6> t6,
            @NotNull Promise<T7> t7,
            @NotNull Promise<T8> t8
    ) {
        List<?> p = Promise.allUntyped(t1, t2, t3, t4, t5, t6, t7, t8);
        return new L8<>((T1) p.get(0), (T2) p.get(1), (T3) p.get(2), (T4) p.get(3), (T5) p.get(4), (T6) p.get(5), (T7) p.get(6), (T8) p.get(7));
    }

    @SuppressWarnings("unchecked")
    @Contract("_, _, _, _, _, _, _, _, _ -> new")
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> L9<T1, T2, T3, T4, T5, T6, T7, T8, T9> all(
            @NotNull Promise<T1> t1,
            @NotNull Promise<T2> t2,
            @NotNull Promise<T3> t3,
            @NotNull Promise<T4> t4,
            @NotNull Promise<T5> t5,
            @NotNull Promise<T6> t6,
            @NotNull Promise<T7> t7,
            @NotNull Promise<T8> t8,
            @NotNull Promise<T9> t9
    ) {
        List<?> p = Promise.allUntyped(t1, t2, t3, t4, t5, t6, t7, t8, t9);
        return new L9<>((T1) p.get(0), (T2) p.get(1), (T3) p.get(2), (T4) p.get(3), (T5) p.get(4), (T6) p.get(5), (T7) p.get(6), (T8) p.get(7), (T9) p.get(8));
    }

    @SuppressWarnings("unchecked")
    @Contract("_, _, _, _, _, _, _, _, _, _ -> new")
    @NotNull
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> L10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> all(
            @NotNull Promise<T1> t1,
            @NotNull Promise<T2> t2,
            @NotNull Promise<T3> t3,
            @NotNull Promise<T4> t4,
            @NotNull Promise<T5> t5,
            @NotNull Promise<T6> t6,
            @NotNull Promise<T7> t7,
            @NotNull Promise<T8> t8,
            @NotNull Promise<T9> t9,
            @NotNull Promise<T10> t10
    ) {
        List<?> p = Promise.allUntyped(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10);
        return new L10<>((T1) p.get(0), (T2) p.get(1), (T3) p.get(2), (T4) p.get(3), (T5) p.get(4), (T6) p.get(5), (T7) p.get(6), (T8) p.get(7), (T9) p.get(8), (T10) p.get(9));
    }

    @NotNull
    @SafeVarargs
    public static <T> ICollectionList<T> allLazyTyped(@NotNull Promise<T>... promises) {
        return ICollectionList.asList(promises).parallelStream().map(Promise::complete).collect(ICollectionList.toCollectionList());
    }

    @NotNull
    public static ICollectionList<?> allUntyped(@NotNull Promise<?>... promises) {
        return ICollectionList.asList(promises).parallelStream().map(Promise::complete).collect(ICollectionList.toCollectionList());
    }
    //</editor-fold>

    public T complete() throws RuntimeException {
        AtomicReference<T> value = new AtomicReference<>();
        AtomicReference<Throwable> throwable = new AtomicReference<>();
        AtomicBoolean isSet = new AtomicBoolean();
        this.executor.submit(id -> {
            current = id;
            value.set(this.value.get());
            throwable.set(this.error.get());
            isSet.set(true);
            synchronized (value) {
                value.notifyAll();
            }
        });
        synchronized (value) {
            while (!isSet.get()) {
                try {
                    value.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        Throwable t = throwable.get();
        if (t != null) {
            if (t instanceof RuntimeException) throw (RuntimeException) t;
            if (t instanceof Error) throw (Error) t;
            throw new RuntimeException(t);
        }
        return value.get();
    }

    @Contract("_ -> new")
    @NotNull
    public static <T> Promise<T> resolve(@Nullable T newValue) {
        return resolve(newValue, false);
    }

    @Contract("_, _ -> new")
    @NotNull
    public static <T> Promise<T> resolve(@Nullable T newValue, boolean pending) {
        return new Promise<>(newValue, pending);
    }

    @Contract("_ -> new")
    @NotNull
    public static <T> Promise<T> reject(@Nullable Throwable throwable) {
        return new Promise<>(throwable);
    }

    @NotNull
    @Override
    public String toString() {
        if (error.get() != null) return "Promise { <rejected> " + error.get() + " }";
        if (value.get() != null || current > 1) return "Promise { " + value.get() + " }";
        return "Promise { <pending> }";
    }

    @Contract(value = "_ -> param1")
    @NotNull
    private <R> Promise<R> setupExecutor(@NotNull Promise<R> promise) {
        promise.executor = this.executor;
        return promise;
    }

    private static final class Cancel extends RuntimeException {
        private static final Cancel INSTANCE = new Cancel();
    }
}
