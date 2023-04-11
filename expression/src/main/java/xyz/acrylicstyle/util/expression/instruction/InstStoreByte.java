package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.nio.ByteBuffer;
import java.util.Deque;

/**
 * Returns: {@link Byte}
 */
public class InstStoreByte extends Instruction {
    private byte n;

    public InstStoreByte() {
        this((byte) 0);
    }

    public InstStoreByte(byte n) {
        super();
        this.n = n;
    }

    @Override
    public void load(@NotNull ByteBuffer buf) {
        n = buf.get();
    }

    @Override
    public void save(@NotNull ByteBuffer buf) {
        buf.put(n);
    }

    @Override
    public Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        return n;
    }

    @Override
    public byte getId() {
        return Opcodes.STORE_BYTE;
    }

    @Override
    public String toString() {
        return "InstStoreByte{" +
                "n=" + n +
                '}';
    }
}
