package util.experimental.javascript;

import com.google.common.annotations.Beta;

import java.util.Iterator;

/**
 * <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/function*">Generator function on MDN Documentation</a><br>
 * Generators are functions that can be exited and later re-entered. Their context (variable bindings) will be saved across re-entrances.<br>
 * Problems:
 * <ul>
 *     <li>It's slow</li>
 *     <li>Race condition</li>
 *     <li>Still experimental and should not be used yet</li>
 *     <li>You'll receive no support for this</li>
 * </ul>
 * Use "yield" keyword for Java 14+. It's better.
 */
@Beta
@FunctionalInterface
public interface GeneratorFunction<T> extends Iterator<GeneratorFunctionResult<T>> {
    T apply(GeneratorFunction<T> function);

    /**
     * Use "this.yield(value)" or "function.yield(value)" or your code will break on Java 14+
     */
    default void yield(T value) {
        // disable broken thing for now
        //Class<?> caller = ReflectionHelper.getCallerClass(2);
        //System.out.println("Caller: " + caller);
        //if (!Ref.getClass(caller).isExtends(GeneratorFunction.class)) {
        //    throw new IllegalStateException("yield method cannot be invoked from caller: " + caller);
        //}
        GeneratorResultHolder.holder.add(this, new GeneratorFunctionResult<>(value, false));
        while (GeneratorResultHolder.holder.containsKey(this)) {
            try {
                synchronized (Thread.currentThread()) {
                    Thread.currentThread().wait(1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    default GeneratorFunction<T> start() {
        Thread thread = new Thread(() -> GeneratorResultHolder.holder.add(this, new GeneratorFunctionResult<>(apply(this), true)));
        thread.start();
        GeneratorLockHolder.holder.add(this, thread);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    default GeneratorFunctionResult<T> next() {
        if (!GeneratorLockHolder.holder.containsKey(this)) this.start();
        while (!GeneratorResultHolder.holder.containsKey(this)) {
            try {
                synchronized (this) {
                    this.wait(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return (GeneratorFunctionResult<T>) GeneratorResultHolder.holder.remove(this);
    }

    @Override
    default boolean hasNext() {
        return GeneratorResultHolder.holder.containsKey(this);
    }
}
