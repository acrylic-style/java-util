package util.memory;

import com.google.common.annotations.Beta;

/**
 * A small class that makes easier to allocate memory and free the memory after.
 */
@Beta
public final class ReservedMemory {
    private byte[] reserve;
    private final int bytes;

    public ReservedMemory(int bytes) {
        this.bytes = bytes;
        reserve = new byte[bytes];
    }

    public void reallocate() {
        this.reserve = new byte[bytes];
    }

    public void free() {
        if (reserve == null) return;
        reserve = null;
        System.gc();
    }

    /**
     * @deprecated just an alias for {@link #free()}
     */
    @Deprecated
    public void freeImmediately() {
        free();
    }
}
