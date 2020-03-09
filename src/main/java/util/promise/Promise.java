package util.promise;

public abstract class Promise<T> implements IPromise<T> {
    private Promise<Object> parent = null;
    private Promise<Object> then = null;
    private Promise<Object> catch_ = null;

    public static <V> Promise<V> async(IPromise<V> promise) {
        return new Promise<V>() {
            @Override
            public V apply(Object o) {
                return promise.apply(o);
            }
        };
    }

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
            if (promise.then != null) {
                return promise.then.apply(promise.apply(o));
            } else {
                if (promise.parent != null) {
                    return promise.apply(promise.parent.apply(o));
                } else {
                    return promise.apply(o);
                }
            }
        } catch (Throwable throwable) {
            if (promise.catch_ != null) {
                promise.catch_.apply(throwable);
                return null;
            } else {
                throw new UncaughtPromiseException(throwable);
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
        promise1.then = this.then;
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
}
