package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.nio.ByteBuffer;
import java.util.Deque;

/**
 * Returns: {@link Double}
 */
public class InstStoreDouble extends Instruction {
    private double n;

    public InstStoreDouble() {
        this(0);
    }

    public InstStoreDouble(double n) {
        super();
        this.n = n;
    }

    @Override
    public void load(@NotNull ByteBuffer buf) {
        n = buf.getDouble();
    }

    @Override
    public void save(@NotNull ByteBuffer buf) {
        buf.putDouble(n);
    }

    @Override
    public Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        return n;
    }

    @Override
    public byte getId() {
        return Opcodes.STORE_DOUBLE;
    }

    @Override
    public String toString() {
        return "InstStoreDouble{" +
                "n=" + n +
                '}';
    }
}
