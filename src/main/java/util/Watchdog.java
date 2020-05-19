package util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

public class Watchdog {
    private boolean started = false;
    private boolean terminated = false;
    private Thread thread;
    private final Thread watchdog;
    private final int timeout;
    private final Object lock = new Object();
    private final Runnable runnable;

    /**
     * Initializes new watchdog instance.
     * @param name Thread name
     * @param runnable Runnable run as asynchronously
     * @param timeout timeout value in ms
     * @param timedOutFunction Runnable that will be run when timed out. (asynchronously)
     */
    public Watchdog(String name, Runnable runnable, int timeout, Runnable timedOutFunction) {
        this.runnable = runnable;
        Thread thread = name == null ? new Thread(runnable) : new Thread(runnable, name);
        Thread watchdog = new Thread(() -> {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (!terminated) {
                        System.out.println("Thread " + thread.getName() + " has elapsed its timeout time, interrupting!");
                        new Thread(timedOutFunction).start();
                        thread.interrupt();
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }
                }
            };
            timer.schedule(task, timeout);
        });
        this.thread = thread;
        this.watchdog = watchdog;
        this.timeout = timeout;
    }

    public Watchdog(String name, Runnable runnable, int timeout) {
        this(name, runnable, timeout, new Thread());
    }

    public Watchdog then(Runnable runnable) {
        if (runnable instanceof RunnableFunction) throw new UnsupportedOperationException("This method cannot be used if you're using RunnableFunction.");
        if (started) throw new IllegalStateException("Thread has already started or ended.");
        Runnable thread2 = new Thread(thread);
        thread = new Thread(() -> {
            thread2.run();
            ((Runnable) new Thread(runnable)).run();
        });
        return this;
    }

    public synchronized void start() {
        if (runnable instanceof RunnableFunction) throw new UnsupportedOperationException("This method cannot be used if you're using RunnableFunction.");
        if (started) throw new IllegalStateException("Thread has already started or ended.");
        Thread thread2 = new Thread(thread);
        thread = new Thread(() -> {
            synchronized (lock) {
                //noinspection CallToThreadRun
                thread2.run();
                terminated = true;
            }
        });
        thread.start();
        watchdog.start();
        started = true;
    }

    public synchronized Object startAwait() {
        if (started) throw new IllegalStateException("Thread has already started or ended.");
        Thread thread2 = new Thread(thread);
        AtomicReference<Object> o = new AtomicReference<>();
        thread = new Thread(() -> {
            if (runnable instanceof RunnableFunction) {
                o.set(((RunnableFunction<?>) runnable).runWithType());
            } else //noinspection CallToThreadRun
                thread2.run();
            terminated = true;
            synchronized (lock) {
                lock.notifyAll();
            }
        });
        synchronized (lock) {
            thread.start();
            watchdog.start();
            try {
                lock.wait(this.timeout);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return o.get();
    }

    // remove of this method causes dependencies to stop working, marking as @Deprecated instead.

    /**
     * @deprecated This method does exactly same as {@link #startAwait()} and does not throw InterruptedException, so use {@link #startAwait()} instead.
     */
    @Deprecated
    public synchronized Object startAwaitWithoutException() {
        return startAwait();
    }
}
