package test.util;

import org.junit.Test;
import util.CollectionList;
import util.promise.Promise;

import java.util.Objects;

import static util.promise.Promise.*;

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
    public void all() {
        Promise<String> promise = async(o -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "A";
        });
        Promise<String> promise2 = async(o -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "B";
        });
        Promise<String> promise3 = async(o -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "C";
        });
        Promise<String> promise4 = async(o -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "D";
        });
        Promise<String> promise5 = async(o -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "E";
        });
        long start = System.currentTimeMillis();
        CollectionList<String> result = Objects.requireNonNull(awaitT(Promise.all(promise, promise2, promise3, promise4, promise5))).map(o -> (String) o);
        long end = System.currentTimeMillis();
        if (end-start > 1500) throw new AssertionError("Took " + (end - start) + " to complete (expected <= 1500ms)");
        assert result.size() == 5;
    }
}
