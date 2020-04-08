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
public abstract class Promise<T> implements IPromise<T> {
    private Promise<Object> parent = null;
    private Promise<Object> then = null;
    private Promise<Object> catch_ = null;
    private PromiseStatus status = PromiseStatus.PENDING;
    private Object v = null;

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static <V> Promise<V> async(IPromise<V> promise) {
        return new Promise<V>() {
            @Override
            public V apply(Object o) {
                return promise.apply(o);
            }
        };
    }

    @Nullable
    public static <V> V awaitT(IPromise<V> iPromise) throws ClassCastException {
        return awaitT(iPromise, null);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <V> V awaitT(IPromise<V> iPromise, Object o) throws ClassCastException {
        Object obj = await(iPromise, o);
        if (obj == null) return null;
        return (V) obj;
    }

    @Nullable
    public static <V> Object await(IPromise<V> iPromise) {
        return await(iPromise, null);
    }

    @Nullable
    public static <V> Object await(IPromise<V> iPromise, Object o) {
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
        try {
            promise.status = PromiseStatus.RESOLVED;
            if (promise.then != null) {
                Object o2 = await(promise.then, promise.apply(o));
                promise.v = o2;
                return o2;
            } else {
                if (promise.parent != null) {
                    V o2 = promise.apply(promise.parent.apply(o));
                    promise.v = o2;
                    return o2;
                } else {
                    V o2 = promise.apply(o);
                    promise.v = o2;
                    return o2;
                }
            }
        } catch (Throwable throwable) {
            promise.status = PromiseStatus.REJECTED;
            promise.v = throwable;
            if (promise.catch_ != null) {
                promise.catch_.apply(throwable);
                return null;
            } else {
                throw new UnhandledPromiseException(throwable);
            }
        }
    }

    public static void queue(IPromise<?> iPromise, Object o) {
        new Thread(() -> {
            Promise<?> promise;
            if (iPromise instanceof Promise) {
                promise = (Promise<?>) iPromise;
            } else {
                promise = new Promise<Object>() {
                    @Override
                    public Object apply(Object o) {
                        return iPromise.apply(o);
                    }
                };
            }
            Object r = promise.apply(o);
            if (promise.then != null) {
                queue(promise.then, r);
            }
        }).start();
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

    public <V> Promise<V> then(IPromise<V> promise) {
        Promise<Object> parent = new Promise<Object>() {
            @Override
            public Object apply(Object o) {
                return Promise.this.apply(o);
            }
        };
        Promise<V> promise1 = new Promise<V>() {
            @Override
            public V apply(Object o) {
                return promise.apply(o);
            }
        };
        promise1.parent = parent;
        this.then = new Promise<Object>() {
            @Override
            public Object apply(Object o) {
                return promise1.apply(o);
            }
        };
        return promise1;
    }

    public <V extends Throwable> Promise<V> catch_(IPromise<V> promise) {
        Promise<V> promise1 = new Promise<V>() {
            @Override
            public V apply(Object o) {
                return promise.apply(o);
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

    @Override
    public abstract T apply(Object o);

    @Override
    public String toString() {
        return "Promise{status=" + status.name().toLowerCase() + ",value=" + v + "}";
    }
}
