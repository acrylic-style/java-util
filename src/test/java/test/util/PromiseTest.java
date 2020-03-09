package test.util;

import org.junit.Test;
import util.ICollectionList;
import util.promise.Promise;

import static util.promise.Promise.async;
import static util.promise.Promise.await;

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
}
