package util.memory;

public final class ReservedMemory {
    @SuppressWarnings({ "unused", "RedundantSuppression" })
    private byte[] reserve;

    public ReservedMemory(int bytes) {
        reserve = new byte[bytes];
    }

    public void free() {
        reserve = null;
        new Thread(System::gc).start(); // run gc in other thread and reduce lags on main thread
    }

    public void freeImmediately() {
        reserve = null;
        System.gc();
    }
}
