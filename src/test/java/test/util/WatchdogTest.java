package test.util;

import org.junit.Test;
import util.RunnableFunction;
import util.Watchdog;

public class WatchdogTest {
    @Test
    public void startAwaitBlocking() {
        try {
            Watchdog watchdog = new Watchdog("WatchdogTest", () -> {
                try {
                    System.out.println("Going to sleep");
                    Thread.sleep(5000);
                    System.out.println("Got up!");
                } catch (InterruptedException e) {
                    System.out.println("Interrupted!");
                    e.printStackTrace();
                }
            }, 1000*3);
            watchdog.startAwait();
            System.out.println("WatchdogTest:startAwaitBlocking has ended!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void startAwaitNoBlocking() {
        try {
            Watchdog watchdog = new Watchdog("WatchdogTest", () -> System.out.println("It shouldn't block."), 1000*3);
            watchdog.startAwait();
            System.out.println("WatchdogTest:startAwaitNoBlocking has ended!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void startAwait() {
        try {
            Watchdog watchdog = new Watchdog("WatchdogTest", new RunnableFunction<String>() {
                @Override
                public String runWithType() {
                    return "Test";
                }
            }, 1000*3);
            String s = (String) watchdog.startAwait();
            System.out.println("Output: " + s);
            System.out.println("WatchdogTest:startAwaitNoBlocking has ended!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runThen() {
        try {
            Watchdog watchdog = new Watchdog("WatchdogTest", () -> System.out.println("A"), 1000*3);
            watchdog.then(() -> System.out.println("B")).then(() -> System.out.println("C"));
            watchdog.startAwait();
            System.out.println("WatchdogTest:runThen has ended!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void start() {
        new Watchdog("WatchdogTest", () -> System.out.println("just start"), 1000).start();
        System.out.println("WatchdogTest:start has ended!");
    }
}
