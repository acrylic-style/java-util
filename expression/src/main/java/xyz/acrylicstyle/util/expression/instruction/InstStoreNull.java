package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.util.Deque;

/**
 * Returns: null
 */
public class InstStoreNull extends Instruction {
    @Override
    public Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        return null;
    }

    @Override
    public byte getId() {
        return Opcodes.STORE_NULL;
    }

    @Override
    public String toString() {
        return "InstStoreNull";
    }
}
