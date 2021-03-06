package util.javascript;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * JavaScript Generator function in Java, but there are limitations:<br />
 * <ul>
 *     <li>You cannot call infinite "while" loop.</li>
 *     <li>yield is actually a Consumer and you must call {@link Consumer#accept(Object) yield.accept(Object)}.</li>
 * </ul>
 * @deprecated Use {@link util.experimental.GeneratorFunction} instead
 */
@Deprecated
public abstract class GeneratorFunction {
    public final Iterator<Object> values;

    public GeneratorFunction(Object... o) {
        List<Object> list = new ArrayList<>();
        apply(list::add, o);
        values = list.iterator();
    }

    public Object next() { return values.next(); }

    public boolean hasNext() { return values.hasNext(); }

    public void forEachRemaining(Consumer<? super Object> consumer) { values.forEachRemaining(consumer); }

    public abstract void apply(Consumer<Object> yield, Object... o);
}
