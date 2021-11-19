package test.util;

import org.junit.jupiter.api.Test;
import util.concurrent.ref.DataCache;

public class DataCacheTest {
    @SuppressWarnings("AssertWithSideEffects")
    @Test
    public void ttlTest() {
        DataCache<String> cache = new DataCache<>("test", 0);
        assert cache.get() == null : cache;
    }

    @Test
    public void ttlTest2() {
        DataCache<String> cache = new DataCache<>("test", System.currentTimeMillis() + 1000 * 1000);
        assert cache.get().equals("test") : cache;
    }

    @Test
    public void ttlTest3() throws InterruptedException {
        DataCache<String> cache = new DataCache<>("test", System.currentTimeMillis() + 500);
        Thread.sleep(600);
        assert !cache.getOptional().isPresent() : cache;
    }
}
