package util.promise;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ActionableResult;
import util.CollectionList;
import util.ICollectionList;
import util.RunnableFunction;
import util.Watchdog;

import java.util.stream.Collectors;

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
        EMPTY_RESOLVED_PROMISE.status = PromiseStatus.RESOLVED;
        EMPTY_REJECTED_PROMISE = new Promise<Object>() {
            @Override
            public Object apply(Object o) {
                return null;
            }
        };
        EMPTY_REJECTED_PROMISE.status = PromiseStatus.REJECTED;
    }

    private Promise<Object> parent = null;
    private Promise<Object> then = null;
    private Promise<Object> catch_ = null;
    private PromiseStatus status = PromiseStatus.PENDING;
    private Object v = null;

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
    protected static CollectionList<Promise<?>> buildChain(Promise<?> promise) {
        CollectionList<Promise<?>> promises = new CollectionList<>();
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
        promise.status = PromiseStatus.RUNNING;
        return tryResolve(promise, o);
    }

    private static <V> void setResolved(Promise<V> promise, V v) {
        promise.status = PromiseStatus.RESOLVED;
        promise.v = v;
    }

    private static void setRejected(Promise<?> promise, Throwable throwable) {
        promise.status = PromiseStatus.REJECTED;
        promise.v = throwable;
    }

    private static <V> Object tryResolve(Promise<V> promise, Object o) {
        if (promise.status == PromiseStatus.RESOLVED) return promise.v;
        try {
            Object result = call(o, promise, buildChain(promise));
            promise.status = PromiseStatus.RESOLVED;
            promise.v = result;
            return result;
        } catch (Throwable throwable) {
            return doReject(promise, throwable);
        }
    }

    private static <V> Object doReject(Promise<V> promise, Throwable throwable) {
        promise.status = PromiseStatus.REJECTED;
        promise.v = throwable;
        return throwIfRejected(promise);
    }

    private static Object throwIfRejected(Promise<?> promise) {
        if (promise.status != PromiseStatus.REJECTED) return null;
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
    protected static <V> Object call(Object o, Promise<V> promise, CollectionList<Promise<?>> chain) throws Throwable {
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
    public static Promise<CollectionList<Object>> all(Promise<?>... promises) {
        return new Promise<CollectionList<Object>>() {
            @Override
            public CollectionList<Object> apply(Object o) {
                return ICollectionList.asList(ICollectionList.asList(promises).parallelStream().map(Promise::await).collect(Collectors.toList()));
            }
        };
    }

    /**
     * Run all promises in parallel then returns the results of promises. All promises must have same type.
     * @param promises List of the promises.
     * @return List of the resolved result of the promises.
     */
    @SafeVarargs
    public static <T> Promise<CollectionList<T>> allTyped(Promise<T>... promises) {
        return new Promise<CollectionList<T>>() {
            @Override
            public CollectionList<T> apply(Object o) {
                return ICollectionList.asList(ICollectionList.asList(promises).parallelStream().map(Promise::complete).collect(Collectors.toList()));
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
    public <V extends Throwable, E> Promise<E> catch_(IPromise<V, E> promise) {
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

    @SuppressWarnings("rawtypes")
    public static final Promise EMPTY_PROMISE = async(o -> o);

    /**
     * Returns the empty promise that passes the arguments as the return value.
     * @return the empty promise
     */
    @SuppressWarnings("unchecked")
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
        if (status == PromiseStatus.RESOLVED || status == PromiseStatus.REJECTED) {
            throwIfRejected(this);
            return (T) this.v;
        }
        status = PromiseStatus.WAITING;
        while (true) {
            if (Thread.interrupted()) {
                Thread.currentThread().interrupt();
                break;
            }
            if (status == PromiseStatus.WAITING) {
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
        if (status == PromiseStatus.PENDING) throw new IllegalStateException("Not expecting PENDING status");
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
        if (status == PromiseStatus.PENDING) this.queue();
        while (true) {
            if (Thread.interrupted()) {
                Thread.currentThread().interrupt();
                break;
            }
            if (status == PromiseStatus.PENDING || status == PromiseStatus.RUNNING || status == PromiseStatus.WAITING) {
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
        if (status == PromiseStatus.PENDING || status == PromiseStatus.WAITING || status == PromiseStatus.RUNNING) throw new IllegalStateException("Not expecting " + this.status.name() + " status");
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
        if (status != PromiseStatus.RESOLVED && status != PromiseStatus.REJECTED) throw new IllegalStateException("Promise isn't resolved nor rejected yet");
        return ActionableResult.ofNullable((T) this.v);
    }

    /**
     * Rejects promise with the value.
     */
    protected void reject(Throwable throwable) {
        setRejected(this, throwable);
    }

    /**
     * Resolves promise with the value.
     */
    protected void resolve(T value) {
        setResolved(this, value);
    }

    @Override
    public String toString() {
        return "Promise{status=" + status.name().toLowerCase() + ",value=" + v + "}";
    }
}
