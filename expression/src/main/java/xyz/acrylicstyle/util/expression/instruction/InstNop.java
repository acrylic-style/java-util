package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.util.Deque;

/**
 * <p>Arguments:</p>
 * <ul>
 *     <li>any argument (1)</li>
 * </ul>
 * <p>Returns: <i>nothing</i></p>
 */
public class InstNop extends Instruction {
    @Override
    public Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        stack.removeLast();
        throw NoReturn.INSTANCE;
    }

    @Override
    public byte getId() {
        return Opcodes.NOP;
    }

    @Override
    public String toString() {
        return "InstNop";
    }
}
