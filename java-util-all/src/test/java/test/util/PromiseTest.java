package test.util;

import org.junit.Test;
import util.CollectionList;
import util.promise.Promise;

import static util.promise.Promise.async;
import static util.promise.Promise.await;

public class PromiseTest {
    @Test
    public void promiseTest() {
        Promise<String> promise = async(o -> "It works! (Data: " + o + ") (PromiseTest#promiseTest)");
        String string = (String) await(promise, "Test");
        assert string != null;
        assert string.equals("It works! (Data: Test) (PromiseTest#promiseTest)");
    }

    @Test
    public void promiseHeavyTask() {
        Promise<String> promise = async(o -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "It works! (Data: " + o + ") (PromiseTest#promiseHeavyTask)";
        });
        String string = (String) await(promise, "Test");
        assert string != null;
        assert string.equals("It works! (Data: Test) (PromiseTest#promiseHeavyTask)");
    }

    @Test
    public void then() {
        String s = async(o -> o + "A")
                .then(o -> o + "B")
                .then(o -> o + "C")
                .then(o -> o + "D")
                .then(o -> o + "E")
                .complete("O");
        assert s.equals("OABCDE") : "string was " + s + " (expected OABCDE)";
    }

    @Test
    public void catchTest() {
        assert async(o -> { throw new RuntimeException("No!"); })
                .catch_(t -> "caught exception!")
                .complete()
                .equals("caught exception!");
    }

    @Test
    public void allTest() {
        Promise<?> p1 = Promise.async(o -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        });
        Promise<?> p2 = Promise.async(o -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 2;
        });
        Promise<?> p3 = Promise.async(o -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 3;
        });
        CollectionList<Object> everything = Promise.all(p1, p2, p3).complete();
        int o1 = (int) everything.get(0);
        int o2 = (int) everything.get(1);
        int o3 = (int) everything.get(2);
        assert o1 == 1 && o2 == 2 && o3 == 3;
    }
}
