package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.nio.ByteBuffer;
import java.util.Deque;

/**
 * Returns: {@link Character}
 */
public class InstStoreChar extends Instruction {
    private char c;

    public InstStoreChar() {
        this('\0');
    }

    public InstStoreChar(char c) {
        super();
        this.c = c;
    }

    @Override
    public void load(@NotNull ByteBuffer buf) {
        c = buf.getChar();
    }

    @Override
    public void save(@NotNull ByteBuffer buf) {
        buf.putChar(c);
    }

    @Override
    public Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        return c;
    }

    @Override
    public byte getId() {
        return Opcodes.STORE_CHAR;
    }

    @Override
    public String toString() {
        return "InstStoreChar{" +
                "c=" + c +
                '}';
    }
}
