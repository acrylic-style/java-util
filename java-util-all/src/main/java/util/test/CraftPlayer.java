package util.test;

// org.bukkit.craftbukkit.???.entity.CraftPlayer
class CraftPlayer implements Player {
    @Override
    public void run() {
        System.out.println("Hello world!");
    }

    public int getPing() { return getHandle().ping; }

    public EntityPlayer getHandle() {
        return new EntityPlayer();
    }
}
