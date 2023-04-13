package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class InstructionSet {
    private final List<Instruction> list = new ArrayList<>();

    public void addAll(@NotNull InstructionSet instructionSet) {
        list.addAll(instructionSet.list);
    }

    public void add(@NotNull Instruction instruction) {
        list.add(instruction);
    }

    public @Nullable Instruction lastOrNull() {
        if (list.isEmpty()) return null;
        return list.get(list.size() - 1);
    }

    public void forEach(@NotNull Consumer<? super Instruction> action) {
        list.forEach(action);
    }

    public Object execute(@NotNull RuntimeData runtimeData) {
        Deque<Object> stack = new ArrayDeque<>();
        for (Instruction inst : list) {
            try {
                Object obj = inst.execute(runtimeData, stack);
                stack.addLast(obj);
            } catch (NoReturn ignored) {}
        }
        return stack.getLast();
    }

    @Override
    public @NotNull String toString() {
        return "InstructionSet[" + list.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }

    public void write(@NotNull ByteBuffer buf) {
        for (Instruction instruction : list) {
            if (instruction.getId() == (byte) 0xFF) continue;
            buf.put(instruction.getId());
            instruction.save(buf);
        }
    }

    public static @NotNull InstructionSet load(@NotNull ByteBuffer buf) {
        InstructionSet instructionSet = new InstructionSet();
        while (buf.hasRemaining()) {
            Instruction instruction = Opcodes.createInstruction(buf.get());
            instruction.load(buf);
            instructionSet.add(instruction);
        }
        return instructionSet;
    }
}
