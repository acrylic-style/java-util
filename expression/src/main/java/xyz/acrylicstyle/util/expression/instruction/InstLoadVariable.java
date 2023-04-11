package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.util.Deque;

/**
 * <p>Arguments:</p>
 * <ul>
 *     <li>String</li>
 * </ul>
 * <p>Returns: {@link Object} (variable data)</p>
 * <p>Throws:</p>
 * <ul>
 *     <li>{@link RuntimeException} if variable was not found by name</li>
 *     <li>{@link ClassCastException} if an argument is not String</li>
 * </ul>
 */
public class InstLoadVariable extends Instruction {
    @Override
    public Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        return runtimeData.getVariable((String) stack.removeLast());
    }

    @Override
    public byte getId() {
        return Opcodes.LOAD_VARIABLE;
    }

    @Override
    public String toString() {
        return "InstLoadVariable";
    }
}
