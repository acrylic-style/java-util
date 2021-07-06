package util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private boolean silent = true;

    public Watchdog(@NotNull Runnable runnable, int timeout) { this(null, runnable, timeout, null); }

    public Watchdog(@Nullable String name, @NotNull Runnable runnable, int timeout) { this(name, runnable, timeout, null); }

    /**
     * Initializes new watchdog instance.
     * @param name Thread name
     * @param runnable Runnable run as asynchronously
     * @param timeout timeout value in ms
     * @param timedOutFunction Runnable that will be run when timed out. (asynchronously)
     */
    public Watchdog(@Nullable String name, @NotNull Runnable runnable, int timeout, @Nullable Runnable timedOutFunction) {
        this.runnable = runnable;
        Thread thread = name == null ? new Thread(runnable) : new Thread(runnable, name);
        Thread watchdog = new Thread(() -> {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (!terminated) {
                        if (!silent) System.out.println("Thread " + thread.getName() + " has elapsed its timeout time, interrupting!");
                        if (timedOutFunction != null) new Thread(timedOutFunction).start();
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

    public Watchdog silent(boolean silent) {
        this.silent = silent;
        return this;
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
                try {
                    //noinspection CallToThreadRun
                    thread2.run();
                } finally {
                    terminated = true;
                }
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
                try {
                    o.set(((RunnableFunction<?>) runnable).runWithType());
                } finally {
                    terminated = true;
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            } else {
                try {//noinspection CallToThreadRun
                    thread2.run();
                } finally {
                    terminated = true;
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
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
}
