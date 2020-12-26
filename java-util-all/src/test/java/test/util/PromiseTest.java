package test.util;

import org.junit.Test;
import util.CollectionList;
import util.promise.IPromise;
import util.promise.Promise;
import util.ref.RejectedFieldOperationException;

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
                Thread.sleep(2000);
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
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 2;
        });
        IPromise<?, ?> p3 = o -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 3;
        };
        CollectionList<Object> everything = Promise.all(p1, p2, p3.build()).complete();
        int o1 = (int) everything.get(0);
        int o2 = (int) everything.get(1);
        int o3 = (int) everything.get(2);
        assert o1 == 1 && o2 == 2 && o3 == 3;
    }

    @Test
    public void callbackTest() {
        Promise<String> promise = new Promise<String>() { // you need to use constructor, you can't use Promise#async(IPromise)
            @Override
            public String apply(Object o) {
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                    resolve("callbacks!");
                }).start();
                return waitUntilResolve();
            }
        }.then(s -> s + "!").then(s -> s + "?");
        assert promise.complete().equals("callbacks!!?") : promise.complete();
    }

    @Test
    public void joinTest() {
        Promise<String> promise = new Promise<String>() {
            @Override
            public String apply(Object o) {
                return "Yes";
            }
        };
        assert promise.join().get().value().equals("Yes") : promise;
    }

    @Test
    public void joinTest2() {
        Promise<String> promise = new Promise<String>() {
            @Override
            public String apply(Object o) throws InterruptedException {
                Thread.sleep(1000000000);
                return "zzz...";
            }
        };
        assert !promise.join(1000).get().isPresent() : promise;
    }

    @Test(expected = RejectedFieldOperationException.class)
    public void freezeTwiceTest() {
        Promise.EMPTY_RESOLVED_PROMISE.freeze();
    }

    @Test(expected = RejectedFieldOperationException.class)
    public void freezeTest() {
        Promise.EMPTY_RESOLVED_PROMISE.complete();
    }
}
