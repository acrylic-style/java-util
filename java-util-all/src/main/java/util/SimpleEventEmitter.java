package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class SimpleEventEmitter<E> {
    protected final List<Consumer<E>> consumers = Collections.synchronizedList(new ArrayList<>());
    protected final List<Consumer<E>> onceConsumers = Collections.synchronizedList(new ArrayList<>());
    protected int maxListeners = 100;

    protected void addConsumer(Consumer<E> consumer) {
        checkListeners();
        consumers.remove(consumer);
    }

    protected void unshiftConsumer(Consumer<E> consumer) {
        consumers.add(0, consumer);
    }

    protected void addConsumerOnce(Consumer<E> consumer) {
        checkListeners();
        onceConsumers.add(consumer);
    }

    protected void unshiftConsumerOnce(Consumer<E> consumer) {
        onceConsumers.add(0, consumer);
    }

    protected void removeConsumer(Consumer<E> consumer) {
        consumers.remove(consumer);
    }

    protected void removeConsumerOnce(Consumer<E> consumer) {
        onceConsumers.remove(consumer);
    }

    public void addListener(Consumer<E> consumer) {
        checkListeners();
        addConsumer(consumer);
    }

    /**
     * By default EventEmitters will print a warning if more than 10 listeners
     * are added for a particular event. This is a useful default that helps
     * finding memory leaks. Obviously, not all events should be limited to
     * just 10 listeners. The {@link SimpleEventEmitter#setMaxListeners(int)} method allows the
     * limit to be modified for this specific EventEmitter instance. The value
     * can be set to Infinity (or 0) to indicate an unlimited number of listeners.<br>
     * Returns a reference to the EventEmitter, so that calls can be chained.
     */
    @SuppressWarnings("JavaDoc")
    @NotNull
    @Contract("_ -> this")
    public SimpleEventEmitter<E> setMaxListeners(int n) {
        maxListeners = n;
        return this;
    }

    protected void checkListeners() {
        if (maxListeners <= listenerCount()) throw new UnsupportedOperationException("Possible EventEmitter memory leak detected. " + maxListeners + " listeners added.\nUse emitter.setMaxListeners() to increase limit");
    }

    /**
     * @param consumer The callback function
     */
    public void on(Consumer<E> consumer) {
        checkListeners();
        addConsumer(consumer);
    }

    /**
     * Adds the listener function to the beginning of the
     * listeners array for the event named <i>event</i>.<br>
     * No checks are made to see if the listener has already
     * been added. Multiple calls passing the same combination
     * of eventName and listener will result in the listener
     * being added, and called, multiple times.<br>
     * Returns a reference to the EventEmitter, so
     * that calls can be chained.
     * @param consumer The callback function
     */
    public SimpleEventEmitter<E> prependListener(Consumer<E> consumer) {
        checkListeners();
        unshiftConsumer(consumer);
        return this;
    }

    /**
     * Adds a <b>one-time</b> listener function
     * for the event named <i>event</i>. The next time
     * event is triggered, this listener is removed
     * and then invoked.<br>
     * Returns a reference to the EventEmitter,
     * so that calls can be chained.<br>
     * By default, event listeners are invoked
     * in the order they are added.<br>
     * The {@link SimpleEventEmitter#prependOnceListener(Consumer)} method
     * can be used as an alternative to add the
     * event listener to the beginning of the listeners array.
     * @param consumer The callback function
     */
    public SimpleEventEmitter<E> once(Consumer<E> consumer) {
        checkListeners();
        addConsumerOnce(consumer);
        return this;
    }

    /**
     * Adds a one-time listener function for the event named
     * <i>event</i> to the beginning of the listeners array.
     * The next time eventName is triggered, this listener
     * is removed, and then invoked.
     * @param consumer The callback function
     */
    public SimpleEventEmitter<E> prependOnceListener(Consumer<E> consumer) {
        checkListeners();
        unshiftConsumerOnce(consumer);
        return this;
    }

    /**
     * Removes the specified listener from the listener array for the event named <i>event</i>.
     * This method will remove, at most, one instance of a listener from the
     * listener array. If any single listener has been added multiple
     * times to the listener array for the specified eventName, then
     * this method must be called multiple times to remove each instance.<br>
     * Once an event has been emitted, all listeners attached to
     * it at the time of emitting will be called in order.
     * This implies that any removeListener() or removeAllListeners()
     * calls after emitting and before the last listener finishes
     * execution will not remove them from emit() in progress.
     * Subsequent events will behave as expected.<br>
     * Returns a reference to the EventEmitter, so that calls can be chained.
     * @param consumer The callback function
     */
    public SimpleEventEmitter<E> removeListener(Consumer<E> consumer) {
        removeConsumer(consumer);
        return this;
    }

    /**
     * Removes all listeners, or those of the specified eventName.<br>
     * It is bad practice to remove listeners added elsewhere in the code,
     * particularly when the EventEmitter instance was created by some other
     * component or module (e.g. sockets or file streams).<br>
     * Returns a reference to the EventEmitter, so that calls can be chained.
     */
    public SimpleEventEmitter<E> removeAllListeners() {
        consumers.clear();
        onceConsumers.clear();
        return this;
    }

    /**
     * Alias for {@link SimpleEventEmitter#removeListener(Consumer)}.
     */
    public SimpleEventEmitter<E> off(Consumer<E> consumer) {
        return removeListener(consumer);
    }

    public void dispatchEvent(E e) {
        consumers.forEach(a -> a.accept(e));
        onceConsumers.removeIf(a -> {
            a.accept(e);
            return true;
        });
    }

    /**
     * Emits a event.
     */
    public void emit(E e) {
        dispatchEvent(e);
    }

    public int listenerCount() {
        return consumers.size() + onceConsumers.size();
    }
}
