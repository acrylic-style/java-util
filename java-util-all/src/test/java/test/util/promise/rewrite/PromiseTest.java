package test.util.promise.rewrite;

import org.junit.Test;
import util.promise.rewrite.Promise;

public class PromiseTest {
    @Test(timeout = 1000)
    public void basicFunctionallyTest() {
        String result = new Promise<String>(context -> "aaa").then(s -> s + "bc").complete();
        assert result.equals("aaabc") : result;
    }

    @SuppressWarnings("ConstantConditions")
    @Test(timeout = 1000, expected = Throwable.class)
    public void exceptionTest() {
        new Promise<String>(context -> {
            if (true) throw new RuntimeException("Oh no!");
        }).onCatch(throwable -> {
            throw new Throwable(throwable);
        }).complete();
        throw new AssertionError("Unreachable code");
    }
}
