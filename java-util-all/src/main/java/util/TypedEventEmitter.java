package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Java implementation of EventEmitter from node.js, but allows Enum as event key.
 * @see EventEmitter
 */
public class TypedEventEmitter<E extends Enum<E>> {
    protected final Collection<E, CollectionList<AConsumer>> consumers = new Collection<>();
    protected final Collection<E, CollectionList<AConsumer>> onceConsumers = new Collection<>();
    protected int maxListeners = 10;

    protected CollectionList<AConsumer> getConsumers(E event) {
        return consumers.containsKey(event) ? consumers.get(event) : new CollectionList<>();
    }

    protected CollectionList<AConsumer> getOnceConsumers(E event) {
        return onceConsumers.containsKey(event) ? onceConsumers.get(event) : new CollectionList<>();
    }

    protected void addConsumer(E event, AConsumer consumer) {
        checkListeners(event);
        CollectionList<AConsumer> consumers2 = getConsumers(event);
        consumers2.add(consumer);
        consumers.removeThenReturnCollection(event).add(event, consumers2);
    }

    protected void unshiftConsumer(E event, AConsumer consumer) {
        CollectionList<AConsumer> consumers2 = getConsumers(event);
        consumers2.unshift(consumer);
        consumers.add(event, consumers2);
    }

    protected void addConsumerOnce(E event, AConsumer consumer) {
        checkListeners(event);
        CollectionList<AConsumer> consumers2 = getOnceConsumers(event);
        consumers2.add(consumer);
        onceConsumers.add(event, consumers2);
    }

    protected void unshiftConsumerOnce(E event, AConsumer consumer) {
        CollectionList<AConsumer> consumers2 = getOnceConsumers(event);
        consumers2.unshift(consumer);
        onceConsumers.add(event, consumers2);
    }

    protected void removeConsumer(E event, AConsumer consumer) {
        CollectionList<AConsumer> consumers2 = getConsumers(event);
        consumers2.remove(consumer);
        consumers.add(event, consumers2);
    }

    protected void removeConsumerOnce(E event, AConsumer consumer) {
        CollectionList<AConsumer> consumers2 = getOnceConsumers(event);
        consumers2.remove(consumer);
        onceConsumers.add(event, consumers2);
    }

    public void addListener(E event, AConsumer consumer) {
        checkListeners(event);
        addConsumer(event, consumer);
    }

    /**
     * By default EventEmitters will print a warning if more than 10 listeners
     * are added for a particular event. This is a useful default that helps
     * finding memory leaks. Obviously, not all events should be limited to
     * just 10 listeners. The {@link TypedEventEmitter#setMaxListeners(int)} method allows the
     * limit to be modified for this specific EventEmitter instance. The value
     * can be set to Infinity (or 0) to indicate an unlimited number of listeners.<br>
     * Returns a reference to the EventEmitter, so that calls can be chained.
     */
    @SuppressWarnings("JavaDoc")
    @NotNull
    @Contract("_ -> this")
    public TypedEventEmitter<E> setMaxListeners(int n) {
        maxListeners = n;
        return this;
    }

    protected void checkListeners(E event) {
        if (maxListeners <= listeners(event).size()) throw new UnsupportedOperationException("Possible EventEmitter memory leak detected. " + maxListeners + " " + event + " listeners added.\nUse emitter.setMaxListeners() to increase limit");
    }

    /**
     * @param event The name of event
     * @param consumer The callback function
     */
    public void on(E event, AConsumer consumer) {
        checkListeners(event);
        addConsumer(event, consumer);
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
     * @param event The name of the event.
     * @param consumer The callback function
     */
    public TypedEventEmitter<E> prependListener(E event, AConsumer consumer) {
        checkListeners(event);
        unshiftConsumer(event, consumer);
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
     * The {@link TypedEventEmitter#prependOnceListener(E, AConsumer)} method
     * can be used as an alternative to add the
     * event listener to the beginning of the listeners array.
     * @param event The name of the event.
     * @param consumer The callback function
     */
    public TypedEventEmitter<E> once(E event, AConsumer consumer) {
        checkListeners(event);
        addConsumerOnce(event, consumer);
        return this;
    }

    /**
     * Adds a one-time listener function for the event named
     * <i>event</i> to the beginning of the listeners array.
     * The next time eventName is triggered, this listener
     * is removed, and then invoked.
     * @param event The name of event
     * @param consumer The callback function
     */
    public TypedEventEmitter<E> prependOnceListener(E event, AConsumer consumer) {
        checkListeners(event);
        unshiftConsumerOnce(event, consumer);
        return this;
    }

    /**
     * Removes the specified listener from the listener array for the event named <i>event</i>.
     * {@link TypedEventEmitter#removeListener(E, AConsumer)}
     * will remove, at most, one instance of a listener from the
     * listener array. If any single listener has been added multiple
     * times to the listener array for the specified eventName, then
     * {@link TypedEventEmitter#removeListener(E, AConsumer)} must
     * be called multiple times to remove each instance.<br>
     * Once an event has been emitted, all listeners attached to
     * it at the time of emitting will be called in order.
     * This implies that any removeListener() or removeAllListeners()
     * calls after emitting and before the last listener finishes
     * execution will not remove them from emit() in progress.
     * Subsequent events will behave as expected.<br>
     * Returns a reference to the EventEmitter, so that calls can be chained.
     * @param event The name of event
     * @param consumer The callback function
     */
    @SuppressWarnings("JavaDoc")
    public TypedEventEmitter<E> removeListener(E event, AConsumer consumer) {
        removeConsumer(event, consumer);
        return this;
    }

    /**
     * Removes all listeners, or those of the specified eventName.<br>
     * It is bad practice to remove listeners added elsewhere in the code,
     * particularly when the EventEmitter instance was created by some other
     * component or module (e.g. sockets or file streams).<br>
     * Returns a reference to the EventEmitter, so that calls can be chained.
     * @param event The name of event
     */
    public TypedEventEmitter<E> removeAllListeners(E event) {
        if (consumers.containsKey(event)) consumers.get(event).forEach(a -> {
            removeConsumer(event, a);
            removeConsumerOnce(event, a);
        });
        return this;
    }

    /**
     * Removes all listeners, or those of the specified eventName.<br>
     * It is bad practice to remove listeners added elsewhere in the code,
     * particularly when the EventEmitter instance was created by some other
     * component or module (e.g. sockets or file streams).<br>
     * Returns a reference to the EventEmitter, so that calls can be chained.
     */
    public TypedEventEmitter<E> removeAllListeners() {
        consumers.clear();
        onceConsumers.clear();
        return this;
    }

    /**
     * Alias for {@link TypedEventEmitter#removeListener(E, AConsumer)}.
     */
    public TypedEventEmitter<E> off(E event, AConsumer consumer) {
        return removeListener(event, consumer);
    }

    public void dispatchEvent(E event, Object... o) {
        getConsumers(event).clone().forEach(a -> a.done(o));
        getOnceConsumers(event).clone().forEach(a -> {
            a.done(o);
            removeConsumerOnce(event, a);
        });
    }

    /**
     * Emits a event.
     * @param event Event name
     * @param o Object
     */
    public void emit(E event, Object... o) {
        dispatchEvent(event, o);
    }

    public int listenerCount(E event) {
        return getConsumers(event).size() + getOnceConsumers(event).size();
    }

    /**
     * Returns a copy of the array of listeners for the event named eventName,
     * including any wrappers (such as those created by {@link TypedEventEmitter#once(E, AConsumer)}).
     * @param event The name of event
     */
    public CollectionList<AConsumer> listeners(E event) {
        return getConsumers(event).concat(getOnceConsumers(event));
    }

    /**
     * Alias for {@link TypedEventEmitter#listeners(E)}.
     */
    public CollectionList<AConsumer> rawListeners(E event) {
        return listeners(event);
    }
}
