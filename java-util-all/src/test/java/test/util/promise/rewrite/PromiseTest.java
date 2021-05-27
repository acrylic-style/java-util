package test.util.promise.rewrite;

import org.junit.Test;
import util.promise.UnhandledPromiseException;
import util.promise.rewrite.Promise;

public class PromiseTest {
    @Test(timeout = 500)
    public void basicFunctionallyTest() {
        String result = new Promise<String>(context -> "aaa").then(s -> s + "bc").complete();
        assert result.equals("aaabc") : result;
    }

    @Test(timeout = 500, expected = Exception.class)
    public void exceptionTest() {
        new Promise<String>(context -> {
            context.reject(new RuntimeException("Oh no!"));
        }).then(Promise.sleepPromise(5000)).onCatch(throwable -> {
            if (!throwable.getMessage().equals("Oh no!")) throw new AssertionError(throwable);
            throw new Exception(throwable);
        }).complete();
        throw new AssertionError("Unreachable code");
    }

    @Test(timeout = 500)
    public void sleepTest() {
        String s = new Promise<String>(context -> {
            Promise.sleepPromise(100).thenDo(o -> context.resolve("aaa")); // it must resolve without invoking #complete
        }).then(Promise.sleep(100)).then(a -> a + "b").then(a -> a + "c").onCatch(throwable -> {
            throwable.printStackTrace();
            throw new AssertionError(throwable);
        }).complete();
        assert s.equals("aaabc") : "expected: aaabc, actual: " + s;
    }

    @Test(timeout = 500, expected = UnhandledPromiseException.class)
    public void unhandledExceptionTest() {
        Promise.reject(new RuntimeException()).complete();
        throw new AssertionError("Unreachable code");
    }
}
