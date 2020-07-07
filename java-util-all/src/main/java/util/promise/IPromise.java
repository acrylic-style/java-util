package util.promise;

public interface IPromise<T, R> {
    R apply(T o);
}
