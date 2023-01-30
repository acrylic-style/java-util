package xyz.acrylicstyle.util.reflector.test;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import xyz.acrylicstyle.util.PerformanceCounter;
import xyz.acrylicstyle.util.reflector.CastTo;
import xyz.acrylicstyle.util.reflector.ConstructorCall;
import xyz.acrylicstyle.util.reflector.FieldGetter;
import xyz.acrylicstyle.util.reflector.ForwardMethod;
import xyz.acrylicstyle.util.reflector.Reflector;
import xyz.acrylicstyle.util.reflector.ReflectorHandler;
import xyz.acrylicstyle.util.reflector.Target;
import xyz.acrylicstyle.util.reflector.TransformParam;
import xyz.acrylicstyle.util.reflector.Type;

@SuppressWarnings("unused")
class ReflectorTest {
    @Test
    public void test() {
        for (int i = 0; i < 1000000; i++) {
            System.nanoTime();
        }
        // CraftPlayer = impl of Player
        // CraftPlayerTest = abstraction of CraftPlayer
        // EntityPlayer4Test = abstraction of EntityPlayer4Test
        Reflector.classLoader = ClassLoader.getSystemClassLoader();
        CraftPlayerTest player = Reflector.newReflector(null, CraftPlayerTest.class, new ReflectorHandler(CraftPlayer.class, new CraftPlayer()));
        System.out.println("Locale: " + player.getLocale());
        EntityPlayer4Test ep = player.getHandle();
        System.out.println("ToString: " + ep.toString());
        System.out.println("Actual Ping: " + ep.getActualPing() + ", expected: 4848");
        int yes = ep.yes();
        System.out.println("'yes' (default method, same as above): " + yes + ", expected: 4848");
        System.out.println("Ping before set: " + ep.getPing() + ", expected: 1212");
        ep.setPing(9999);
        System.out.println("Ping after set: " + ep.getPing() + ", expected: 9999");
        ep.setPing(1234);
        PerformanceCounter pc = new PerformanceCounter(PerformanceCounter.Unit.NANOSECONDS);
        long start = System.nanoTime();
        for (int i = 0; i < 50000; i++) {
            pc.recordStart();
            ep.getPingFor(ep);
            pc.recordEnd();
        }
        System.out.println("Time to getPingFor: " + (System.nanoTime() - start) + "ns (" + ((System.nanoTime() - start) / 1_000_000.0) + "ms)");
        int pingFor = ep.getPingFor(ep);
        System.out.println("Result of getPingFor:\n" + pc.getDetails(true));
        System.out.println("Ping for ep: " + pingFor + ", expected: 1234");
        EntityPlayer4Test newPlayer = Constructors.make().createEntityPlayer(1555);
        System.out.println("Ping of new constructor: " + newPlayer.getPing() + ", expected: 1555");
        AnotherPlayer anotherPlayer = Reflector.newReflector(null, AnotherPlayer.class, new ReflectorHandler(CraftPlayer.class, Reflector.unwrapOrObject(player)));
        System.out.println("Another player #getTag: " + anotherPlayer.getTag() + ", expected: a");
        System.out.println("Another player #getLocale: " + anotherPlayer.getLocale() + ", expected: en");
    }

    // org.bukkit.entity.Player
    private interface Player {
        @Target(clazz = CraftPlayer.class)
        @ForwardMethod("getLocale()Ljava/lang/String;")
        String getLocale();
    }

    // abstraction of Player
    private interface AnotherPlayer extends Player {
        String getTag();
    }

    // obc.entity.CraftPlayer
    private static class CraftPlayer implements Player {
        @Override
        public String getLocale() {
            return "en";
        }

        public EntityPlayer getHandle() {
            return new EntityPlayer(1212);
        }

        public String getTag() {
            return "a";
        }
    }

    // abstraction of CraftPlayer
    private interface CraftPlayerTest extends ReflectorHandler.ClazzGetter {
        @CastTo(EntityPlayer4Test.class)
        EntityPlayer4Test getHandle();
        String getLocale();
    }

    // nms.EntityPlayer
    private static class EntityPlayer {
        // we can get the value via #getPing, and can be set via #setPing
        public int ping;

        public EntityPlayer(int ping) {
            this.ping = ping;
        }

        @Override
        public String toString() {
            return "EntityPlayer{ping="+ping+"}";
        }

        // never invoked
        public int getPing() { return 2424; }

        // invoked via #getActualPing
        public int getSomething() { return 4848; } // suppose this method as obfuscated method name, it actually returns a ping

        public int getPingFor(EntityPlayer player) {
            return player.ping;
        }
    }

    private interface Constructors {
        @Contract(pure = true)
        static @NotNull Constructors make() {
            return Reflector.newEmptyReflector(null, Constructors.class);
        }

        @ConstructorCall(EntityPlayer4Test.class)
        @CastTo(EntityPlayer4Test.class)
        EntityPlayer4Test createEntityPlayer(int ping);
    }

    // abstraction of EntityPlayer
    private interface EntityPlayer4Test {
        @FieldGetter // if this annotation is present, it will get value from field "ping", instead of just invoking "getPing".
        int getPing();

        //@FieldSetter // it's actually not needed, it finds field automatically.
        void setPing(int ping);

        @ForwardMethod("getSomething") // invoke #getSomething in original method instead
        int getActualPing();
        String toString();

        @ForwardMethod("getPingFor(Lxyz/acrylicstyle/util/reflector/test/ReflectorTest$EntityPlayer;)I")
        int getPingFor(@TransformParam EntityPlayer4Test player);

        default int yes() {
            return getActualPing();
        }
    }
}
