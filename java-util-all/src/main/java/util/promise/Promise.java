package util.promise;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.CollectionList;
import util.ICollectionList;

import java.util.stream.Collectors;

/**
 * Represents partial implementation of JavaScript Promise.
 * @param <T> Promise return type
 */
public abstract class Promise<T> implements IPromise<Object, T> {
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
            public V apply(Object o) {
                return promise.apply(o);
            }
        };
    }

    @Nullable
    public static <V> V awaitT(IPromise<Object, V> iPromise) throws ClassCastException {
        return awaitT(iPromise, null);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <V> V awaitT(IPromise<Object, V> iPromise, Object o) throws ClassCastException {
        Object obj = await(iPromise, o);
        if (obj == null) return null;
        return (V) obj;
    }

    public static <V> Promise<V> of(V v) {
        return async(o -> v);
    }

    @Nullable
    public static <V> Object await(IPromise<Object, V> iPromise) {
        return await(iPromise, null);
    }

    /**
     * Builds the promise chain that will be used to resolve promise.
     * This may take a long time if promise chain is long.
     * @param promise the last promise that has parent, no then
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
                public V apply(Object o) {
                    return iPromise.apply(o);
                }
            };
        }
        if (promise.status == PromiseStatus.RESOLVED) return promise.v;
        CollectionList<Promise<?>> chain = buildChain(promise);
        try {
            Object rs = call(o, promise, chain);
            promise.status = PromiseStatus.RESOLVED;
            return rs;
        } catch (Throwable throwable) {
            promise.status = PromiseStatus.REJECTED;
            promise.v = throwable;
            if (promise.catch_ != null) {
                return promise.catch_.apply(throwable);
            } else {
                throw new UnhandledPromiseException(throwable);
            }
        }
    }

    @Nullable
    protected static <V> Object call(Object o, Promise<V> promise, CollectionList<Promise<?>> chain) {
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
    public void queue(Object o) { queue(this, o); }

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
     * Run all promises at parallel then return.
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

    @SuppressWarnings("unchecked")
    public <V> Promise<V> then(IPromise<T, V> promise) {
        Promise<V> promise1 = new Promise<V>() {
            @Override
            public V apply(Object o) {
                return promise.apply((T) o);
            }
        };
        promise1.parent = (Promise<Object>) this;
        this.then = new Promise<Object>() {
            @Override
            public Object apply(Object o) {
                return promise.apply((T) o);
            }
        };
        return promise1;
    }

    @SuppressWarnings("unchecked")
    public <V extends Throwable, E> Promise<E> catch_(IPromise<V, E> promise) {
        Promise<E> promise1 = new Promise<E>() {
            @Override
            public E apply(Object o) {
                return promise.apply((V) o);
            }
        };
        promise1.parent = new Promise<Object>() {
            @Override
            public Object apply(Object o) {
                return Promise.this.apply(o);
            }
        };
        promise1.catch_ = new Promise<Object>() {
            @Override
            public Object apply(Object o) {
                return promise1.apply(o);
            }
        };
        this.catch_ = promise1.catch_;
        return promise1;
    }

    @SuppressWarnings("rawtypes")
    public static final Promise EMPTY_PROMISE = async(o -> o);

    @SuppressWarnings("unchecked")
    public static <T> Promise<T> getEmptyPromise() {
        return EMPTY_PROMISE;
    }

    public static Promise<Object> sleepAsync(long millis) {
        return async(o -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return o;
        });
    }

    @Override
    public abstract T apply(Object o);

    @Override
    public String toString() {
        return "Promise{status=" + status.name().toLowerCase() + ",value=" + v + "}";
    }
}
