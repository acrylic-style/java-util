package util.promise;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ActionableResult;
import util.CollectionList;
import util.ICollectionList;
import util.RunnableFunction;
import util.Watchdog;
import util.ref.AtomicReferencePredicateUpdater;
import util.ref.RejectedFieldOperationException;

import java.util.function.Consumer;

/**
 * Represents partial implementation of JavaScript Promise.
 * @param <T> Promise return type
 */
public abstract class Promise<T> implements IPromise<Object, T> {
    public static final Promise<?> EMPTY_RESOLVED_PROMISE;
    public static final Promise<?> EMPTY_REJECTED_PROMISE;

    static {
        EMPTY_RESOLVED_PROMISE = new Promise<Object>() {
            @Override
            public Object apply(Object o) {
                return null;
            }
        };
        EMPTY_RESOLVED_PROMISE.setStatus(PromiseStatus.RESOLVED);
        EMPTY_RESOLVED_PROMISE.freeze();
        EMPTY_REJECTED_PROMISE = new Promise<Object>() {
            @Override
            public Object apply(Object o) {
                return null;
            }
        };
        EMPTY_REJECTED_PROMISE.setStatus(PromiseStatus.REJECTED);
        EMPTY_REJECTED_PROMISE.freeze();
    }

    private Promise<Object> parent = null;
    private Promise<Object> then = null;
    private Promise<Object> catch_ = null;
    private final AtomicReferencePredicateUpdater<Boolean> frozen = new AtomicReferencePredicateUpdater<>((oldValue, newValue) -> !oldValue, false).setterRejectedMessage("This promise is already set to frozen");
    private final AtomicReferencePredicateUpdater<PromiseStatus> status = new AtomicReferencePredicateUpdater<>((oldValue, newValue) -> !frozen.get(), PromiseStatus.PENDING).setterRejectedMessage("Cannot modify the status of this promise, this promise is frozen");
    private Object v = null;

    private void setStatus(@NotNull PromiseStatus status) throws RejectedFieldOperationException {
        this.status.set(status);
    }

    @NotNull
    public PromiseStatus getStatus() {
        return this.status.get();
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static <V> Promise<V> async(IPromise<Object, V> promise) {
        return new Promise<V>() {
            @Override
            public V apply(Object o) throws Throwable {
                return promise.apply(o);
            }
        };
    }

    public static <V> V awaitT(IPromise<Object, V> iPromise) throws ClassCastException {
        return awaitT(iPromise, null);
    }

    @SuppressWarnings("unchecked")
    public static <V> V awaitT(IPromise<Object, V> iPromise, Object o) throws ClassCastException {
        Object obj = await(iPromise, o);
        if (obj == null) return null;
        return (V) obj;
    }

    public static <V> Promise<V> of(V v) {
        return async(o -> v);
    }

    public static <V> Object await(IPromise<Object, V> iPromise) {
        return await(iPromise, null);
    }

    /**
     * Builds the promise chain that will be used to resolve promise.
     * @param promise the last promise that has parent, no "then"
     * @return the promise chain
     */
    protected static CollectionList<?, Promise<?>> buildChain(Promise<?> promise) {
        CollectionList<?, Promise<?>> promises = new CollectionList<>();
        promises.add(promise);
        while (promise.parent != null) {
            promises.add(promise.parent);
            promise = promise.parent;
        }
        return promises.reverse().clone();
    }

    /**
     * Wait for the promise to complete.
     * Calling of this method causes thread to be blocked
     * until the promise is resolved or rejected.
     * @param iPromise the promise that will be run
     * @param o the object that will be passed to the promise
     * @return the result of the promise
     */
    public static <V> Object await(IPromise<Object, V> iPromise, Object o) {
        Promise<V> promise;
        if (iPromise instanceof Promise) {
            promise = (Promise<V>) iPromise;
        } else {
            promise = new Promise<V>() {
                @Override
                public V apply(Object o) throws Throwable {
                    return iPromise.apply(o);
                }
            };
        }
        promise.setStatus(PromiseStatus.RUNNING);
        return tryResolve(promise, o);
    }

    private static <V> void setResolved(Promise<V> promise, V v) {
        if (promise.status.isNot(PromiseStatus.PENDING) && promise.status.isNot(PromiseStatus.WAITING) && promise.status.isNot(PromiseStatus.RUNNING))
            throw new IllegalStateException("the promise is already resolved or rejected!");
        promise.setStatus(PromiseStatus.RESOLVED);
        promise.v = v;
    }

    private static void setRejected(Promise<?> promise, Throwable throwable) {
        if (promise.status.isNot(PromiseStatus.PENDING) && promise.status.isNot(PromiseStatus.WAITING) && promise.status.isNot(PromiseStatus.RUNNING))
            throw new IllegalStateException("the promise is already resolved or rejected!");
        promise.setStatus(PromiseStatus.REJECTED);
        promise.v = throwable;
    }

    private static <V> Object tryResolve(Promise<V> promise, Object o) {
        if (promise.status.is(PromiseStatus.RESOLVED)) return promise.v;
        throwIfRejected(promise);
        try {
            promise.setStatus(PromiseStatus.RUNNING);
            Object result = call(o, promise, buildChain(promise));
            promise.setStatus(PromiseStatus.RESOLVED);
            promise.v = result;
            return result;
        } catch (Throwable throwable) {
            return doReject(promise, throwable);
        }
    }

    private static <V> Object doReject(Promise<V> promise, Throwable throwable) {
        promise.setStatus(PromiseStatus.REJECTED);
        promise.v = throwable;
        return throwIfRejected(promise);
    }

    private static Object throwIfRejected(Promise<?> promise) {
        if (promise.status.isNot(PromiseStatus.REJECTED)) return null;
        if (promise.catch_ != null) {
            try {
                return promise.catch_.apply(promise.v);
            } catch (Throwable e) {
                throw new UnhandledPromiseException(e);
            }
        } else {
            throw new UnhandledPromiseException((Throwable) promise.v);
        }
    }

    @Nullable
    protected static <V> Object call(Object o, Promise<V> promise, CollectionList<?, Promise<?>> chain) throws Throwable {
        if (promise.then != null) {
            chain = buildChain(promise.then);
            Object obj = o;
            for (Promise<?> p : chain) obj = p.apply(obj);
            return promise.v = obj;
        } else {
            Object obj = o;
            for (Promise<?> p : chain) obj = p.apply(obj);
            return promise.v = obj;
        }
    }

    public static void queue(IPromise<Object, ?> iPromise, Object o) {
        new Thread(() -> await(iPromise, o)).start();
    }

    /**
     * Queue running the promise.<br />
     * It'll just run at the Thread and then runs Thread#start. There are no way to stop it when you've started.
     */
    public void queue() { queue(null); }

    /**
     * Queue running the promise with object.
     * @param o Object that will be provided to promise.
     */
    public Promise<T> queue(Object o) {
        queue(this, o);
        return this;
    }

    /**
     * Non-static awaitT.
     * @param o Object that will be provided to promise.
     * @return Result of the promise.
     */
    public T complete(Object o) { return awaitT(this, o); }

    /**
     * Non-static awaitT. Null will be provided to promise instead of the object.
     * @return Result of the promise.
     */
    public T complete() { return awaitT(this, null); }

    /**
     * Run all promises in parallel then returns the results of promises.
     * @param promises List of the promises.
     * @return List of the resolved result of the promises.
     */
    @SuppressWarnings("unchecked")
    public static Promise<CollectionList<?, Object>> all(Promise<?>... promises) {
        return Promise.allTyped((Promise<Object>[]) promises);
    }

    /**
     * Run all promises in parallel then returns the results of promises. All promises must have same type.
     * @param promises List of the promises.
     * @return List of the resolved result of the promises.
     */
    @SafeVarargs
    public static <T> Promise<CollectionList<?, T>> allTyped(Promise<T>... promises) {
        return new Promise<CollectionList<?, T>>() {
            @Override
            public CollectionList<?, T> apply(Object o) {
                return ICollectionList.asList(promises).parallelStream().map(Promise::complete).collect(ICollectionList.toCollectionList());
            }
        };
    }

    @SuppressWarnings("unchecked")
    public <V> Promise<V> then(IPromise<T, V> promise) {
        Promise<V> promise1 = new Promise<V>() {
            @Override
            public V apply(Object o) throws Throwable {
                return promise.apply((T) o);
            }
        };
        promise1.parent = (Promise<Object>) this;
        this.then = new Promise<Object>() {
            @Override
            public Object apply(Object o) throws Throwable {
                return promise.apply((T) o);
            }
        };
        return promise1;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public Promise<T> thenDo(@NotNull Consumer<T> action) {
        Promise<T> promise1 = new Promise<T>() {
            @Override
            public T apply(Object o) {
                T t = (T) o;
                action.accept(t);
                return t;
            }
        };
        promise1.parent = (Promise<Object>) this;
        this.then = new Promise<Object>() {
            @Override
            public Object apply(Object o) {
                action.accept((T) o);
                return this;
            }
        };
        return promise1;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <V extends Throwable, E> Promise<E> catch_(@NotNull IPromise<V, E> promise) {
        Promise<E> promise1 = new Promise<E>() {
            @Override
            public E apply(Object o) throws Throwable {
                return promise.apply((V) o);
            }
        };
        promise1.parent = new Promise<Object>() {
            @Override
            public Object apply(Object o) throws Throwable {
                return Promise.this.apply(o);
            }
        };
        promise1.catch_ = new Promise<Object>() {
            @Override
            public Object apply(Object o) throws Throwable {
                return promise1.apply(o);
            }
        };
        this.catch_ = promise1.catch_;
        return promise1;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <V extends Throwable> Promise<T> catchDo(@NotNull Consumer<V> action) {
        Promise<T> promise1 = new Promise<T>() {
            @Override
            public T apply(Object o) {
                action.accept((V) o);
                return (T) o;
            }
        };
        promise1.parent = new Promise<Object>() {
            @Override
            public Object apply(Object o) throws Throwable {
                return Promise.this.apply(o);
            }
        };
        promise1.catch_ = new Promise<Object>() {
            @Override
            public Object apply(Object o) throws Throwable {
                return promise1.apply(o);
            }
        };
        this.catch_ = promise1.catch_;
        return promise1;
    }

    @SuppressWarnings("rawtypes")
    @NotNull
    public static final Promise EMPTY_PROMISE = async(o -> o);

    /**
     * Returns the empty promise that passes the arguments as the return value.
     * @return the empty promise
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> Promise<T> getEmptyPromise() { return EMPTY_PROMISE; }

    /**
     * Returns the sleeping promise that sleeps until the time has elapsed, then returns the argument.
     * @param millis the time to sleep
     */
    @NotNull
    public static Promise<Object> sleepAsync(long millis) {
        return async(o -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            }
            return o;
        });
    }

    @Override
    public abstract T apply(Object o) throws Throwable;

    /**
     * Waits forever until the promise is resolved / rejected.
     * Useful when resolving the callback / converting the callback to the promise.
     */
    @SuppressWarnings("unchecked")
    protected T waitUntilResolve() {
        if (this.status.is(PromiseStatus.RESOLVED) || this.status.is(PromiseStatus.REJECTED)) {
            throwIfRejected(this);
            return (T) this.v;
        }
        this.setStatus(PromiseStatus.WAITING);
        while (true) {
            if (Thread.interrupted()) {
                Thread.currentThread().interrupt();
                break;
            }
            if (this.status.is(PromiseStatus.WAITING)) {
                try {
                    synchronized (this) {
                        this.wait(1);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } else break;
        }
        if (this.status.is(PromiseStatus.PENDING)) throw new IllegalStateException("Not expecting PENDING status");
        throwIfRejected(this);
        return (T) this.v;
    }

    /**
     * Waits until the promise is resolved / rejected.
     * Useful when resolving the callback / converting the callback to the promise.
     * @param timeout the timeout in milliseconds
     * @return the resolved or rejected value, or null if timed out execution
     */
    @SuppressWarnings("unchecked")
    @Nullable
    protected T waitUntilResolve(int timeout) {
        return (T) new Watchdog(new RunnableFunction<T>() {
            @Override
            public T runWithType() {
                return waitUntilResolve();
            }
        }, timeout).silent(true).startAwait();
    }

    @NotNull
    public Promise<T> join(int timeout) {
        new Watchdog(this::join, timeout).silent(true).startAwait();
        throwIfRejected(this);
        return this;
    }

    @NotNull
    public Promise<T> join() {
        if (this.status.is(PromiseStatus.PENDING)) this.queue();
        while (true) {
            if (Thread.interrupted()) {
                Thread.currentThread().interrupt();
                break;
            }
            if (this.status.is(PromiseStatus.PENDING) || this.status.is(PromiseStatus.RUNNING) || this.status.is(PromiseStatus.WAITING)) {
                try {
                    synchronized (this) {
                        this.wait(1);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } else break;
        }
        if (this.status.is(PromiseStatus.PENDING) || this.status.is(PromiseStatus.WAITING) || this.status.is(PromiseStatus.RUNNING))
            throw new IllegalStateException("Not expecting " + this.status.get().name() + " status");
        throwIfRejected(this);
        return this;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public ActionableResult<T> get() {
        return ActionableResult.ofNullable((T) this.v);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public ActionableResult<T> getIfResolved() {
        if (status.isNot(PromiseStatus.RESOLVED) && status.isNot(PromiseStatus.REJECTED))
            throw new IllegalStateException("Promise isn't resolved nor rejected yet");
        return ActionableResult.ofNullable((T) this.v);
    }

    /**
     * Rejects promise with the value.
     */
    public void reject(Throwable throwable) { setRejected(this, throwable); }

    /**
     * Resolves promise with the value.
     */
    public void resolve(T value) { setResolved(this, value); }

    @SuppressWarnings("unchecked")
    public void resolveWithObject(Object value) { setResolved(this, (T) value); }

    public void freeze() { frozen.set(true); }

    @Override
    public String toString() {
        return "Promise{status=" + getStatus().name().toLowerCase() + ",value=" + v + "}";
    }
}
