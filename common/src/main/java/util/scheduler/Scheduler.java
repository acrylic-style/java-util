package util.scheduler;

import util.Validate;
import util.base.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @deprecated Will be removed in 0.17.
 */
@Deprecated
public class Scheduler<T> {
    private final int delay;

    public Scheduler() { this(1000); }

    public Scheduler(int delay) { this.delay = delay; }

    private final List<Consumer<T>> consumers = new ArrayList<>();
    private final List<T> values = new ArrayList<>();
    private final List<Integer> delays = new ArrayList<>();

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
                Integer delay = Lists.first(delays);
                if (delay != null && delay > 0) sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            consumers.get(0).accept(Lists.first(values));
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
