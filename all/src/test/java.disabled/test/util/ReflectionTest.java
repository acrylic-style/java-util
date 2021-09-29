package test.util;

import org.junit.Test;
import util.reflect.ReflectionHelper;
import util.reflect.Ref;

import java.util.Arrays;

public class ReflectionTest {
    @Test
    public void isExtendsTest() {
        assert Ref.getClass(TestClass.class).isExtends(Runnable.class);
        assert !Ref.getClass(TestClass.class).isExtends(Thread.class);

        assert Ref.getClass(TestClass2.class).isExtends(Runnable.class);
        assert Ref.getClass(TestClass2.class).isExtends(Thread.class);

        assert Ref.getClass(TestClass3.class).isExtends(Runnable.class);
        assert Ref.getClass(TestClass3.class).isExtends(Thread.class);
        assert Ref.getClass(TestClass3.class).isExtends(TestClass2.class);
    }

    @Test
    public void callerClassTest() {
        TestClass.doTest();
        assert TestClass.caller.equals(ReflectionTest.class) : TestClass.caller.getCanonicalName() + ", all frames: " + Arrays.toString(TestClass.elements);
    }

    private static class TestClass implements Runnable {
        static Class<?> caller = null;
        static StackTraceElement[] elements = null;

        @Override
        public void run() {}

        public static void doTest() {
            caller = ReflectionHelper.getCallerClass();
            elements = new Throwable().getStackTrace();
        }
    }

    private static class TestClass2 extends Thread {
        @Override
        public void run() {}
    }

    private static class TestClass3 extends TestClass2 {
        @Override
        public void run() {}
    }
}
