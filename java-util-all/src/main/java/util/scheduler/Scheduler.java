package util.scheduler;

import util.CollectionList;
import util.Validate;

import java.util.Objects;
import java.util.function.Consumer;

public class Scheduler<T> {
    private final int delay;

    public Scheduler() { this(1000); }

    public Scheduler(int delay) { this.delay = delay; }

    private final CollectionList<Consumer<T>> consumers = new CollectionList<>();
    private final CollectionList<T> values = new CollectionList<>();
    private final CollectionList<Integer> delays = new CollectionList<>();

    public Scheduler<T> schedule(Consumer<T> consumer, T arg, Integer minimumDelay) {
        Validate.notNull(consumer, "consumer cannot be null");
        if (minimumDelay == null) minimumDelay = 0;
        if (minimumDelay < 0) throw new IllegalArgumentException("minimumDelay < 0");
        delays.add(minimumDelay);
        values.add(arg);
        consumers.add(consumer);
        if (consumers.size() == 1) new SchedulerExecutor().start();
        return this;
    }

    private class SchedulerExecutor extends Thread {
        public void run() {
            try {
                Integer delay = delays.first();
                if (delay != null && delay > 0) sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Objects.requireNonNull(consumers.first()).accept(values.first());
            try {
                sleep(Scheduler.this.delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            values.remove(0);
            delays.remove(0);
            consumers.remove(0);
            if (consumers.size() > 0) new SchedulerExecutor().start();
        }
    }
}
