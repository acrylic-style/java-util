package util.promise;

public interface IPromise<T, R> {
    R apply(T o) throws Throwable;

    default Promise<R> build() {
        return new Promise<R>() {
            @SuppressWarnings("unchecked")
            @Override
            public R apply(Object o) throws Throwable {
                return IPromise.this.apply((T) o);
            }
        };
    }
}
