package util.promise;

import org.jetbrains.annotations.Nullable;

public abstract class Promise<T> implements IPromise<T> {
    private Promise<Object> parent = null;
    private Promise<Object> then = null;
    private Promise<Object> catch_ = null;
    private PromiseStatus status = PromiseStatus.PENDING;
    private Object v = null;

    public static <V> Promise<V> async(IPromise<V> promise) {
        return new Promise<V>() {
            @Override
            public V apply(Object o) {
                return promise.apply(o);
            }
        };
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
                Object o2 = promise.then.apply(promise.apply(o));
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
        //promise1.then = this.then;
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
