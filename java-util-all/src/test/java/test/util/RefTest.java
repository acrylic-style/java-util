package test.util;

import org.junit.Test;
import util.reflect.Ref;

public class RefTest {
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

    private static class TestClass implements Runnable {
        @Override
        public void run() {}
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
