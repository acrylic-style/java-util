package test.util;

import org.junit.Test;
import util.function.RunnableSupplier;
import util.Watchdog;

public class WatchdogTest {
    @Test
    public void startAwaitBlocking() {
        long start = System.currentTimeMillis();
        new Watchdog("WatchdogTest (Block)", () -> {
            try {
                Thread.sleep(500000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 1000).silent(true).startAwait();
        long end = System.currentTimeMillis();
        assert end-start < 1500;
    }

    @Test
    public void startAwaitNoBlocking() {
        long start = System.currentTimeMillis();
        Watchdog watchdog = new Watchdog("WatchdogTestNoBlocking", () -> {}, 1000*3);
        watchdog.startAwait();
        long end = System.currentTimeMillis();
        assert end-start < 500;
    }

    @Test
    public void startAwait() {
        Watchdog watchdog = new Watchdog("WatchdogTest", new RunnableSupplier<String>() {
            @Override
            public String runWithType() {
                return "Test";
            }
        }, 1000*3);
        String s = (String) watchdog.startAwait();
        assert s.equals("Test");
    }
}
