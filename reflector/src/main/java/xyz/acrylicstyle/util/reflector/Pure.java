package xyz.acrylicstyle.util.reflector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Specifies that the annotated method has no visible side effects.
 * If its return value is not used, removing its invocation won't
 * affect program state and change the semantics, unless method call throws an exception.
 * Exception throwing is not considered to be a side effect.
 * <p>
 * Method should not be marked as pure if it does not produce a side effect by itself,
 * but it could be used to establish a happens-before relation between an event in
 * another thread, so changes performed in another thread might become visible in current thread
 * after this method invocation. Examples of such methods are {@link Object#wait()}, {@link Thread#join()}
 * or {@link AtomicBoolean#get()}. On the other hand, some synchronized methods like {@link java.util.Vector#get(int)}
 * could be marked as pure, because the purpose of synchronization here is to keep the collection internal integrity
 * rather than to wait for an event in another thread.
 * <p>
 * "Invisible" side effects (such as logging) that don't affect the "important" program semantics are allowed.
 * <p>
 * Using <code>@Pure</code> annotation for method allows the reflector to optimize the code by caching the values.
 * Reflector annotations such as {@link FieldSetter} has side effects and cannot be marked as pure.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Pure {
}
