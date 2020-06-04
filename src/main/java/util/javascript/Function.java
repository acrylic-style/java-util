package util.javascript;

import com.google.common.annotations.Beta;

@FunctionalInterface
@Beta
public interface Function<T> {
    /**
     * Gets a result.
     *
     * @return a result
     */
    T $(Object... o);
}