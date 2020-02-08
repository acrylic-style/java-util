package test.util;

import org.junit.Test;
import util.EventEmitter;

public class EventEmitterTest extends EventEmitter {
    @Test
    public void onAndEmit() {
        this.on("test", (o) -> {
            String a = (String) o[0];
            System.out.println("Received input: " + a);
        });
        this.emit("test", "Hello o/ on");
        assert this.listeners("test").size() == 1;
    }

    @Test
    public void onceAndEmit() {
        this.once("test", (o) -> {
            String a = (String) o[0];
            System.out.println("Received input: " + a);
        });
        this.emit("test", "Hello o/ once");
        assert this.listeners("test").size() == 0;
    }
}
