package test.util;

import org.junit.Test;
import util.CollectionList;
import util.ICollectionList;
import util.promise.Promise;
import util.promise.UnhandledPromiseException;

import java.util.Objects;

import static util.promise.Promise.*;

public class PromiseTest {
    @Test
    public void promiseTest() {
        Promise<String> promise = async(o -> "It works! (Data: " + o + ") (PromiseTest#promiseTest)");
        String string = (String) await(promise, "Test");
        System.out.println(string);
    }

    @Test
    public void promiseHeavyTask() {
        Promise<String> promise = async(o -> {
            try {
                System.out.println("Gonna sleep. Don't disturb me.");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "It works! (Data: " + o + ") (PromiseTest#promiseHeavyTask)";
        });
        String string = (String) await(promise, "Test");
        System.out.println(string);
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
        long start = System.currentTimeMillis();
        CollectionList<String> result = Objects.requireNonNull(awaitT(Promise.all(promise, promise, promise, promise, promise))).map(o -> (String) o);
        long end = System.currentTimeMillis();
        if (end-start > 1500) throw new AssertionError("Took " + (end - start) + " to complete (expected <= 1500ms)");
        System.out.println(result.join(", "));
        assert result.size() == 5;
    }

    @Test
    public void promiseThen() {
        Promise<String> promise = async(o -> "J,u,s,t,," + o);
        String[] string = (String[]) await(promise.then(o -> {
            String s = (String) o;
            return s.split(",");
        }), "B,u,r,n,,C,o,o,k,i,e");
        System.out.println(ICollectionList.asList(string).join(""));
    }

    @Test
    public void promiseThrowable() {
        Promise<String> promise = async(o -> {
            throw new RuntimeException("???");
        });
        String s = (String) await(promise.catch_(e -> {
            System.out.println("An error occurred inside the Promise: " + e);
            Throwable throwable = (Throwable) e;
            throwable.printStackTrace();
            return null;
        }), "no");
        System.out.println("s: " + s);
    }

    @Test(expected = UnhandledPromiseException.class)
    public void promiseUnhandledThrowable() {
        Promise<String> promise = async(o -> {
            throw new RuntimeException("???");
        });
        String s = (String) await(promise, "no");
        System.out.println("s: " + s);
    }
}
