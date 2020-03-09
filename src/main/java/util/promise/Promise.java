package util.promise;
/*
public abstract class Promise<T> implements IPromise<T> {
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

    public static <V> V await(IPromise<V> iPromise, Object o) {
        Promise<Object> promise = new Promise<Object>() {
            @Override
            public Object apply(Object o) {
                return iPromise.apply(o);
            }
        };
        try {
            return promise.apply(o);
        } catch (Throwable throwable) {
            return promise.catch_.apply(o);
        }
    }

    public <V> Promise<V> then(IPromise<V> promise) {
        Promise<V> promise1 = new Promise<V>() {
            @Override
            public V apply(Object o) {
                return promise.apply(o);
            }
        };
        this.then = new Promise<Object>() {
            @Override
            public Object apply(Object o) {
                return promise1.apply(o);
            }
        };
        return promise1;
    }

    public <V> Promise<V> catch_(IPromise<V> promise) {
        Promise<V> promise1 = new Promise<V>() {
            @Override
            public V apply(Object o) {
                if (!(o instanceof Throwable)) throw new IllegalArgumentException("Object must be throwable");
                return promise.apply(o);
            }
        };
        this.catch_ = new Promise<Object>() {
            @Override
            public Object apply(Object o) {
                return promise1.apply(o);
            }
        };
        return promise1;
    }

    @Override
    public abstract T apply(Object o);
}
*/