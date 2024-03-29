package util.eventEmitter;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.base.Lists;
import util.function.AConsumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java implementation of EventEmitter from node.js.
 * @see TypedEventEmitter
 */
public class EventEmitter {
    protected final Map<String, List<AConsumer>> consumers = new HashMap<>();
    protected final Map<String, List<AConsumer>> onceConsumers = new HashMap<>();
    protected int maxListeners = 10;

    protected List<AConsumer> getConsumers(String event) {
        return consumers.containsKey(event) ? consumers.get(event) : new ArrayList<>();
    }

    protected List<AConsumer> getOnceConsumers(String event) {
        return onceConsumers.containsKey(event) ? onceConsumers.get(event) : new ArrayList<>();
    }

    protected void addConsumer(String event, AConsumer consumer) {
        checkListeners(event);
        List<AConsumer> consumers2 = getConsumers(event);
        consumers2.add(consumer);
        consumers.put(event, consumers2);
    }

    protected void unshiftConsumer(String event, AConsumer consumer) {
        List<AConsumer> consumers2 = getConsumers(event);
        Lists.unshift(consumers2, consumer);
        consumers2.add(consumer);
        consumers.put(event, consumers2);
    }

    protected void addConsumerOnce(String event, AConsumer consumer) {
        checkListeners(event);
        List<AConsumer> consumers2 = getOnceConsumers(event);
        consumers2.add(consumer);
        onceConsumers.put(event, consumers2);
    }

    protected void unshiftConsumerOnce(String event, AConsumer consumer) {
        List<AConsumer> consumers2 = getOnceConsumers(event);
        Lists.unshift(consumers2, consumer);
        onceConsumers.put(event, consumers2);
    }

    protected void removeConsumer(String event, AConsumer consumer) {
        List<AConsumer> consumers2 = getConsumers(event);
        consumers2.remove(consumer);
        consumers.put(event, consumers2);
    }

    protected void removeConsumerOnce(String event, AConsumer consumer) {
        List<AConsumer> consumers2 = getOnceConsumers(event);
        consumers2.remove(consumer);
        onceConsumers.put(event, consumers2);
    }

    public void addListener(String event, AConsumer consumer) {
        checkListeners(event);
        addConsumer(event, consumer);
    }

    /**
     * By default EventEmitters will print a warning if more than 10 listeners
     * are added for a particular event. This is a useful default that helps
     * finding memory leaks. Obviously, not all events should be limited to
     * just 10 listeners. The {@link EventEmitter#setMaxListeners(int)} method allows the
     * limit to be modified for this specific EventEmitter instance. The value
     * can be set to Infinity (or 0) to indicate an unlimited number of listeners.<br>
     * Returns a reference to the EventEmitter, so that calls can be chained.
     */
    @SuppressWarnings("JavaDoc")
    @NotNull
    @Contract("_ -> this")
    public EventEmitter setMaxListeners(int n) {
        maxListeners = n;
        return this;
    }

    protected void checkListeners(String event) {
        if (maxListeners <= listeners(event).size()) throw new UnsupportedOperationException("Possible EventEmitter memory leak detected. " + maxListeners + " " + event + " listeners added.\nUse emitter.setMaxListeners() to increase limit");
    }

    /**
     * @param event The name of event
     * @param consumer The callback function
     */
    public void on(String event, AConsumer consumer) {
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
    public EventEmitter prependListener(String event, AConsumer consumer) {
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
     * The {@link EventEmitter#prependOnceListener(String, AConsumer)} method
     * can be used as an alternative to add the
     * event listener to the beginning of the listeners array.
     * @param event The name of the event.
     * @param consumer The callback function
     */
    public EventEmitter once(String event, AConsumer consumer) {
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
    public EventEmitter prependOnceListener(String event, AConsumer consumer) {
        checkListeners(event);
        unshiftConsumerOnce(event, consumer);
        return this;
    }

    /**
     * Removes the specified listener from the listener array for the event named <i>event</i>.
     * {@link EventEmitter#removeListener(String, AConsumer)}
     * will remove, at most, one instance of a listener from the
     * listener array. If any single listener has been added multiple
     * times to the listener array for the specified eventName, then
     * {@link EventEmitter#removeListener(String, AConsumer)} must
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
    public EventEmitter removeListener(String event, AConsumer consumer) {
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
    public EventEmitter removeAllListeners(String event) {
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
    public EventEmitter removeAllListeners() {
        consumers.clear();
        onceConsumers.clear();
        return this;
    }

    /**
     * Alias for {@link EventEmitter#removeListener(String, AConsumer)}.
     */
    public EventEmitter off(String event, AConsumer consumer) {
        return removeListener(event, consumer);
    }

    public void dispatchEvent(String event, Object... o) {
        new ArrayList<>(getConsumers(event)).forEach(a -> a.done(o));
        new ArrayList<>(getOnceConsumers(event)).forEach(a -> {
            a.done(o);
            removeConsumerOnce(event, a);
        });
    }

    /**
     * Emits a event.
     * @param event Event name
     * @param o Object
     */
    public void emit(String event, Object... o) {
        dispatchEvent(event, o);
    }

    public int listenerCount(String event) {
        return getConsumers(event).size() + getOnceConsumers(event).size();
    }

    /**
     * Returns a copy of the array of listeners for the event named eventName,
     * including any wrappers (such as those created by {@link EventEmitter#once(String, AConsumer)}).
     * @param event The name of event
     */
    public List<AConsumer> listeners(String event) {
        return Lists.concat(getConsumers(event), getOnceConsumers(event));
    }

    /**
     * Alias for {@link EventEmitter#listeners(String)}.
     */
    public List<AConsumer> rawListeners(String event) {
        return listeners(event);
    }
}
