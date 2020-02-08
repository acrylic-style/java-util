package util;

import java.util.Timer;
import java.util.TimerTask;

public class Watchdog {
    private boolean started = false;
    private boolean terminated = false;
    private Thread thread;
    private Thread watchdog;
    private int timeout;
    private final Object lock = new Object();

    public Watchdog(String name, Runnable runnable, int timeout) {
        Thread thread = name == null ? new Thread(runnable) : new Thread(runnable, name);
        Thread watchdog = new Thread(() -> {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (!terminated) {
                        System.out.println("Thread " + thread.getName() + " has elapsed its timeout time, interrupting!");
                        thread.interrupt();
                    }
                }
            };
            timer.schedule(task, timeout);
        });
        this.thread = thread;
        this.watchdog = watchdog;
        this.timeout = timeout;
    }

    public Watchdog then(Runnable runnable) {
        if (started) throw new IllegalStateException("Thread has already started or ended.");
        Runnable thread2 = new Thread(thread);
        thread = new Thread(() -> {
            thread2.run();
            ((Runnable) new Thread(runnable)).run();
        });
        return this;
    }

    public synchronized void start() {
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

    public synchronized void startAwait() throws InterruptedException {
        if (started) throw new IllegalStateException("Thread has already started or ended.");
        Thread thread2 = new Thread(thread);
        thread = new Thread(() -> {
            synchronized (lock) {
                //noinspection CallToThreadRun
                thread2.run();
                terminated = true;
                lock.notifyAll();
            }
        });
        synchronized (lock) {
            thread.start();
            watchdog.start();
            lock.wait(this.timeout);
        }
    }
}
