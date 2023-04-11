package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.nio.ByteBuffer;
import java.util.Deque;

/**
 * Returns: {@link Short}
 */
public class InstStoreShort extends Instruction {
    private short n;

    public InstStoreShort() {
        this((short) 0);
    }

    public InstStoreShort(short n) {
        super();
        this.n = n;
    }

    @Override
    public void load(@NotNull ByteBuffer buf) {
        n = buf.getShort();
    }

    @Override
    public void save(@NotNull ByteBuffer buf) {
        buf.putShort(n);
    }

    @Override
    public Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        return n;
    }

    @Override
    public byte getId() {
        return Opcodes.STORE_SHORT;
    }

    @Override
    public String toString() {
        return "InstStoreShort{" +
                "n=" + n +
                '}';
    }
}
