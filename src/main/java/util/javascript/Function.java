package util.javascript;

import com.google.common.annotations.Beta;

@FunctionalInterface
@Beta
public interface Function {
    /**
     * Gets a result.
     *
     * @return a result
     */
    Object $(Object... o);

    @SuppressWarnings("unchecked")
    default  <A> A $$(Object... o) { return (A) $(o); }
}