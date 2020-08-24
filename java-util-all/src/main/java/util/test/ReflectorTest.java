package util.test;

class ReflectorTest {
    public static void main(String[] args) {
        CraftPlayerTest player = Reflector.newReflector(ClassLoader.getSystemClassLoader(), CraftPlayerTest.class, new ReflectorHandler(CraftPlayer.class, new PlayerImpl()));
        System.out.println(player.getClazz());
        System.out.println(Reflector.castTo(CraftPlayerTest.class, player, "getHandle", EntityPlayer4Test.class));
    }

    public static class PlayerImpl implements CraftPlayer {
        @Override
        public String getLocale() {
            return "en";
        }

        @Override
        public EntityPlayer getHandle() {
            return new EntityPlayer();
        }
    }

    // org.bukkit.entity.Player
    public interface Player {
        String getLocale();
    }

    // org.bukkit.craftbukkit.entity.CraftPlayer
    public interface CraftPlayer extends Player {
        EntityPlayer getHandle();
    }

    public interface CraftPlayerTest extends ReflectorHandler.ClazzGetter {
        EntityPlayer4Test getHandle();
    }

    // nms.EntityPlayer
    public static class EntityPlayer {
        public int getPing() {
            return 1212;
        }
    }

    public interface EntityPlayer4Test {
        int getPing();
    }
}
