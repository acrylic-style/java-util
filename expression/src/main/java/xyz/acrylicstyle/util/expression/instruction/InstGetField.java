package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Deque;

/**
 * <p>Arguments:</p>
 * <ul>
 *     <li>instance</li>
 * </ul>
 * <p>Returns: {@link Object} (value returned from field)</p>
 */
public class InstGetField extends Instruction {
    private String clazz;
    private String name;

    public InstGetField() {
    }

    public InstGetField(@NotNull Field field) {
        this.clazz = field.getDeclaringClass().getTypeName();
        this.name = field.getName();
    }

    public InstGetField(@Nullable String clazz, @Nullable String name) {
        super();
        this.clazz = clazz;
        this.name = name;
    }

    @Override
    public void load(@NotNull ByteBuffer buf) {
        this.clazz = getString(buf);
        this.name = getString(buf);
    }

    @Override
    public void save(@NotNull ByteBuffer buf) {
        putString(buf, clazz);
        putString(buf, name);
    }

    @Override
    public Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        try {
            return Class.forName(this.clazz).getField(this.name).get(stack.removeLast());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte getId() {
        return Opcodes.GET_FIELD;
    }

    @Override
    public String toString() {
        return "InstGetField{" +
                "clazz='" + clazz + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
