package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.nio.ByteBuffer;
import java.util.Deque;

/**
 * Returns: {@link Integer}
 */
public class InstStoreInt extends Instruction {
    private int n;

    public InstStoreInt() {
        this(0);
    }

    public InstStoreInt(int n) {
        super();
        this.n = n;
    }

    @Override
    public void load(@NotNull ByteBuffer buf) {
        n = buf.getInt();
    }

    @Override
    public void save(@NotNull ByteBuffer buf) {
        buf.putInt(n);
    }

    @Override
    public Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        return n;
    }

    @Override
    public byte getId() {
        return Opcodes.STORE_INT;
    }

    @Override
    public String toString() {
        return "InstStoreInt{" +
                "n=" + n +
                '}';
    }
}
