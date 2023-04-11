package xyz.acrylicstyle.util.expression;

import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.expression.instruction.InstGetField;
import xyz.acrylicstyle.util.expression.instruction.InstLoadVariable;
import xyz.acrylicstyle.util.expression.instruction.InstStoreDouble;
import xyz.acrylicstyle.util.expression.instruction.InstStoreString;
import xyz.acrylicstyle.util.expression.instruction.InstInvokeVirtual;
import xyz.acrylicstyle.util.expression.instruction.InstNop;
import xyz.acrylicstyle.util.expression.instruction.InstStoreBoolean;
import xyz.acrylicstyle.util.expression.instruction.InstStoreByte;
import xyz.acrylicstyle.util.expression.instruction.InstStoreChar;
import xyz.acrylicstyle.util.expression.instruction.InstStoreFloat;
import xyz.acrylicstyle.util.expression.instruction.InstStoreInt;
import xyz.acrylicstyle.util.expression.instruction.InstStoreLong;
import xyz.acrylicstyle.util.expression.instruction.InstStoreNull;
import xyz.acrylicstyle.util.expression.instruction.InstStoreShort;
import xyz.acrylicstyle.util.expression.instruction.Instruction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class Opcodes {
    private static final Map<Byte, Supplier<Instruction>> INSTRUCTION_MAP = new HashMap<>();

    public static final byte NOP = 0x00;
    public static final byte STORE_BOOLEAN = 0x01;
    public static final byte STORE_BYTE = 0x02;
    public static final byte STORE_CHAR = 0x03;
    public static final byte STORE_DOUBLE = 0x04;
    public static final byte STORE_FLOAT = 0x05;
    public static final byte STORE_INT = 0x06;
    public static final byte STORE_LONG = 0x07;
    public static final byte STORE_SHORT = 0x08;
    public static final byte STORE_STRING = 0x09;
    public static final byte STORE_NULL = 0x0A;
    public static final byte LOAD_VARIABLE = 0x0B;
    public static final byte INVOKE_VIRTUAL = 0x0C;
    public static final byte GET_FIELD = 0x0D;

    public static @NotNull Instruction createInstruction(byte opcode) {
        return Objects.requireNonNull(INSTRUCTION_MAP.get(opcode), "Unregistered opcode " + opcode).get();
    }

    static {
        INSTRUCTION_MAP.put(NOP, InstNop::new);
        INSTRUCTION_MAP.put(STORE_BOOLEAN, InstStoreBoolean::new);
        INSTRUCTION_MAP.put(STORE_BYTE, InstStoreByte::new);
        INSTRUCTION_MAP.put(STORE_CHAR, InstStoreChar::new);
        INSTRUCTION_MAP.put(STORE_DOUBLE, InstStoreDouble::new);
        INSTRUCTION_MAP.put(STORE_FLOAT, InstStoreFloat::new);
        INSTRUCTION_MAP.put(STORE_INT, InstStoreInt::new);
        INSTRUCTION_MAP.put(STORE_LONG, InstStoreLong::new);
        INSTRUCTION_MAP.put(STORE_SHORT, InstStoreShort::new);
        INSTRUCTION_MAP.put(STORE_STRING, InstStoreString::new);
        INSTRUCTION_MAP.put(STORE_NULL, InstStoreNull::new);
        INSTRUCTION_MAP.put(LOAD_VARIABLE, InstLoadVariable::new);
        INSTRUCTION_MAP.put(INVOKE_VIRTUAL, InstInvokeVirtual::new);
        INSTRUCTION_MAP.put(GET_FIELD, InstGetField::new);
    }
}
