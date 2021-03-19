package util.promise.rewrite;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Validate;
import util.concurrent.MoreExecutorService;
import util.function.ThrowableConsumer;
import util.function.ThrowableFunction;
import util.promise.atomic.AtomicThrowable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

// TODO: unhandled promise exception
// TODO: Promise.all
public class Promise<T> {
    private final MoreExecutorService executor;
    private final AtomicReference<T> value = new AtomicReference<>();
    private final AtomicReference<Throwable> error = new AtomicReference<>();
    private int current = 0;

    private Promise(@Nullable T value, boolean pending) {
        this.executor = MoreExecutorService.of(Executors.newSingleThreadExecutor());
        this.value.set(value);
        if (!pending) {
            this.executor.submit(id -> current = id);
        }
    }

    private Promise(@Nullable Throwable throwable) {
        this.executor = MoreExecutorService.of(Executors.newSingleThreadExecutor());
        this.error.set(throwable);
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
        this.executor.submit(id -> {
            try {
                current = id;
                T val = supplier.run(new PromiseContext<T>() {
                    @Override
                    public void resolve(@Nullable T value) {
                        Promise.this.value.set(value);
                    }

                    @Override
                    public void reject(@Nullable Throwable throwable) {
                        Promise.this.error.set(throwable);
                        throw new RuntimeException("cancel");
                    }
                });
                if (current == id && !(supplier instanceof ThrowableConsumer.ThrowableConsumerFunction)) {
                    value.set(val);
                }
            } catch (Throwable throwable) {
                if (throwable instanceof RuntimeException && throwable.getMessage().equals("cancel")) return;
                if (current == id) {
                    error.set(throwable);
                }
            }
        });
    }

    @NotNull
    public Promise<T> thenDo(@NotNull ThrowableConsumer<T> action) {
        return then(action.toFunction());
    }

    @NotNull
    public <R> Promise<R> then(@NotNull ThrowableFunction<T, R> function) {
        if (error.get() != null) return reject(error.get());
        Promise<R> promise = resolve(null, true);
        this.executor.submit(id -> {
            try {
                current = id;
                R val = function.run(this.value.get());
                promise.value.set(val);
            } catch (Throwable throwable) {
                if (throwable instanceof RuntimeException && throwable.getMessage().equals("cancel")) return;
                promise.error.set(throwable);
            }
        });
        return promise;
    }

    @NotNull
    public Promise<T> onCatch(@NotNull ThrowableConsumer<Throwable> action) {
        this.executor.submit(id -> {
            current = id;
            try {
                Throwable throwable = error.get();
                if (throwable != null) {
                    error.set(null);
                    action.accept(throwable);
                }
            } catch (Throwable throwable1) {
                error.set(throwable1);
            }
        });
        return this;
    }

    public T complete() {
        AtomicReference<T> value = new AtomicReference<>();
        AtomicThrowable throwable = new AtomicThrowable();
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
        throwable.throwAsRuntimeExceptionIfSet();
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
        if (value.get() != null || current != 0) return "Promise { " + value.get() + " }";
        return "Promise { <pending> }";
    }
}
