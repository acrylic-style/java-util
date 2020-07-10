package util.test;

import util.inject.Injector;
import util.inject.LoadOrder;

class InjectorTest {
    public static void main(String[] args) {
        Injector.traceConstructor("util.test.CraftPlayer");
        //Injector.inject(EntityPlayerAccessor.class, "util.inject.test.EntityPlayer"); // needs to be registered first to transform and create new method with new return type
        //Injector.inject(ITest.class, "util.inject.test.CraftPlayer"); // allows CraftPlayer to be casted to ITest and defines _getHandle() with new return type
        Player player = getPlayer();
        ITest test = (ITest) player;
        System.out.println("Ping (ITest->CraftPlayer->#getPing): " + test.getPing()); // getPing() should return 1212
        System.out.println("Ping (ITest#_getHandle->CraftPlayer#getHandle->#getPing): " + test._getHandle().getPing()); // also this should return 1212
        System.out.println("Ping (ITest#_getHandle->CraftPlayer#getHandle->ping field): " + test._getHandle().ping);
    }

    public static Player getPlayer() {
        return new CraftPlayer();
    }

    public static abstract class EntityPlayerAccessor {
        public Object ping; // the actual type is int, but it can be any type! (if compatible)

        public abstract int getPing();
    }

    public interface ITest {
        void run(); // this method exposes Player#run()
        int getPing(); // and this method exposes CraftPlayer#getPing()

        /*
         * injected on Line 7, so this method will exist at runtime, but comes with under bar.
         * This method requires to inject EntityPlayerAccessor first to generate this method.
         * Note: the method with under bar is always available.
         */
        @LoadOrder(LoadOrder.LoadPriority.LOWEST)
        EntityPlayerAccessor _getHandle();
    }
}
