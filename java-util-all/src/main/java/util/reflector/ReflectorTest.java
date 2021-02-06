package util.reflector;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
class ReflectorTest {
    public static void main(String[] args) {
        // CraftPlayer = impl of Player
        // CraftPlayerTest = abstraction of CraftPlayer
        // EntityPlayer4Test = abstraction of EntityPlayer4Test
        CraftPlayerTest player = Reflector.newReflector(ClassLoader.getSystemClassLoader(), CraftPlayerTest.class, new ReflectorHandler(CraftPlayer.class, new CraftPlayer()));
        System.out.println("Locale: " + player.getLocale());
        EntityPlayer4Test ep = player.getHandle();
        System.out.println("ToString: " + ep.toString());
        System.out.println("Actual Ping: " + ep.getActualPing() + ", expected: 4848");
        System.out.println("Ping before set: " + ep.getPing() + ", expected: 1212");
        ep.setPing(9999);
        System.out.println("Ping after set: " + ep.getPing() + ", expected: 9999");
        int pingFor = ep.getPingFor(ep);
        System.out.println("Ping for ep: " + pingFor + ", expected: 1234");
        int yes = ep.yes();
        System.out.println("'yes' (default method): " + yes + ", expected: 4848");
        EntityPlayer4Test newPlayer = Constructors.make().createEntityPlayer(1555);
        System.out.println("Ping of new constructor: " + newPlayer.getPing() + ", expected: 1555");
    }

    // org.bukkit.entity.Player
    private interface Player {
        String getLocale();
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
    }

    // abstraction of CraftPlayer
    private interface CraftPlayerTest extends ReflectorHandler.ClazzGetter {
        @CastTo(EntityPlayer4Test.class)
        EntityPlayer4Test getHandle();
        String getLocale();
    }

    // nms.EntityPlayer
    private static class EntityPlayer {
        // can be get via #getPing, and can be set via #setPing
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
            return 1234;
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
        int getPingFor(@TransformParam EntityPlayer4Test player);

        default int yes() {
            return getActualPing();
        }
    }
}
