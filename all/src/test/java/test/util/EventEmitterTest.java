package test.util;

import org.junit.jupiter.api.Test;
import util.eventEmitter.EventEmitter;

public class EventEmitterTest extends EventEmitter {
    @Test
    public void onAndEmit() {
        this.on("test_", (o) -> {
            /* do something */
        });
        this.emit("test_", "Hello o/ on");
        assert this.listeners("test_").size() == 1 : "Expected 1 listener count, but got " + this.listeners("test_").size();
    }

    @Test
    public void onceAndEmit() {
        this.once("test_once", (o) -> {
            /* do something */
        });
        this.emit("test_once", "Hello o/ once");
        assert this.listeners("test_once").size() == 0 : "Expected 0 listener count, but got " + this.listeners("test_test").size();
    }
}
