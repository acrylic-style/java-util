package util.javascript;

@FunctionalInterface
public interface Function<T> {
    /**
     * Gets a result.
     *
     * @return a result
     */
    T $(Object... o);
}