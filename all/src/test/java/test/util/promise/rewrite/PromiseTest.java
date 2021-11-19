package test.util.promise.rewrite;

import org.junit.jupiter.api.Test;
import util.promise.UnhandledPromiseException;
import util.promise.rewrite.Promise;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class PromiseTest {
    @Test
    public void basicFunctionallyTest() {
        assertTimeout(Duration.ofMillis(500), () -> {
            String result = new Promise<String>(context -> "aaa").then(s -> s + "bc").complete();
            assert result.equals("aaabc") : result;
        });
    }

    @Test
    public void exceptionTest() {
        assertTimeout(Duration.ofMillis(500), () -> {
            assertThrows(Exception.class, () -> {
                new Promise<String>(context -> {
                    context.reject(new RuntimeException("Oh no!"));
                }).then(Promise.sleepPromise(5000)).onCatch(throwable -> {
                    if (!throwable.getMessage().equals("Oh no!")) throw new AssertionError(throwable);
                    throw new Exception(throwable);
                }).complete();
                throw new AssertionError("Unreachable code");
            });
        });
    }

    @Test
    public void sleepTest() {
        assertTimeout(Duration.ofMillis(500), () -> {
            String s = new Promise<String>(context -> {
                Promise.sleepPromise(100).thenDo(o -> context.resolve("aaa")); // it must resolve without invoking #complete
            }).then(Promise.sleep(100)).then(a -> a + "b").then(a -> a + "c").onCatch(throwable -> {
                throwable.printStackTrace();
                throw new AssertionError(throwable);
            }).complete();
            assert s.equals("aaabc") : "expected: aaabc, actual: " + s;
        });
    }

    @Test
    public void unhandledExceptionTest() {
        assertTimeout(Duration.ofSeconds(500), () -> {
            assertThrows(UnhandledPromiseException.class, () -> {
                Promise.reject(new RuntimeException()).complete();
                throw new AssertionError("Unreachable code");
            });
        });
    }

    @Test
    public void thenDo() {
        assertTimeout(Duration.ofMillis(500), () -> {
            String s = new Promise<String>(context -> {
                Promise.sleepPromise(100).thenDo(o -> {
                    context.resolve("aaa");
                }); // it must resolve without invoking #complete
            }).thenDo(e -> {
            }).thenDo(Promise.sleepPromise(100)).thenDo(Promise.sleepPromise(100)).thenDo(Promise.sleepPromise(100)).then(a -> a + "b").then(a -> a + "c").onCatch(throwable -> {
                throwable.printStackTrace();
                throw new AssertionError(throwable);
            }).complete();
            assert s.equals("aaabc") : "expected: aaabc, actual: " + s;
        });
    }
}
