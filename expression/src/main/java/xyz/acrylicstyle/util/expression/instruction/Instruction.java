package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Deque;

public abstract class Instruction {
    public void load(@NotNull ByteBuffer buf) {
    }

    public void save(@NotNull ByteBuffer buf) {
    }

    public abstract Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack);

    /**
     * Returns the id of this instruction. 0xFF instructs the compiler not to save this instruction.
     * @return id
     */
    public abstract byte getId();

    public static @NotNull String getString(@NotNull ByteBuffer buf) {
        byte[] bytes = new byte[buf.getInt()];
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void putString(@NotNull ByteBuffer buf, @NotNull String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        buf.putInt(bytes.length);
        buf.put(bytes);
    }
}
