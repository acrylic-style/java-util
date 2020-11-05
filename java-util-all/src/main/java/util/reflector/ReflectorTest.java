package util.reflector;

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
    }

    // org.bukkit.entity.Player
    public interface Player {
        String getLocale();
    }

    // org.bukkit.craftbukkit.entity.CraftPlayer
    public static class CraftPlayer implements Player {
        @Override
        public String getLocale() {
            return "en";
        }

        public EntityPlayer getHandle() {
            return new EntityPlayer();
        }
    }

    // abstraction of CraftPlayer
    public interface CraftPlayerTest extends ReflectorHandler.ClazzGetter {
        @CastTo(EntityPlayer4Test.class)
        EntityPlayer4Test getHandle();
        String getLocale();
    }

    // nms.EntityPlayer
    public static class EntityPlayer {
        // can be get via #getPing, and can be set via #setPing
        public int ping = 1212;

        @Override
        public String toString() {
            return "EntityPlayer{ping="+ping+"}";
        }

        // never invoked
        public int getPing() { return 2424; }

        // invoked via #getActualPing
        public int getSomething() { return 4848; } // suppose this method as obfuscated method name, it actually returns a ping
    }

    // abstraction of EntityPlayer
    public interface EntityPlayer4Test {
        @FieldGetter // if this annotation is present, it will get value from field "ping", instead of just invoking "getPing".
        int getPing();

        void setPing(int ping);

        @ForwardMethod("getSomething")
        int getActualPing();
        String toString();
    }
}
