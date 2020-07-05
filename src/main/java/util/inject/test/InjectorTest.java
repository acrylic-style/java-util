package util.inject.test;

import util.inject.Injector;

class InjectorTest {
    public static void main(String[] args) {
        Injector.inject(ITest.class, "util.inject.test.CraftPlayer"); // allows CraftPlayer to be casted to ITest
        Injector.inject(EntityPlayerAccessor.class, "util.inject.test.EntityPlayer"); // it has no effect right now...
        Player player = obtainPlayerSomehow();
        ITest test = (ITest) player;
        System.out.println("Ping(ITest->CraftPlayer->#getPing): " + test.getPing()); // getPing() should return 1212
        System.out.println("Ping (raw): " + test.getHandle().ping); // this isn't possible btw
    }

    public static Player obtainPlayerSomehow() {
        return new CraftPlayer();
    }

    public static class EntityPlayerAccessor {
        public int ping;
    }

    public interface ITest {
        void run(); // this method exposes Player#run()
        int getPing(); // and this method exposes CraftPlayer#getPing()
        EntityPlayerAccessor getHandle(); // ??? (currently this isn't possible because of AbstractMethodError)
    }
}
