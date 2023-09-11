package xyz.acrylicstyle.expression.test;

import org.junit.jupiter.api.Test;
import xyz.acrylicstyle.util.InvalidArgumentException;
import xyz.acrylicstyle.util.PerformanceCounter;
import xyz.acrylicstyle.util.expression.CompileData;
import xyz.acrylicstyle.util.expression.ExpressionParser;
import xyz.acrylicstyle.util.expression.RuntimeData;
import xyz.acrylicstyle.util.expression.instruction.DummyInstruction;
import xyz.acrylicstyle.util.expression.instruction.InstructionSet;

public class ExpressionTest {
    @Test
    public void test() throws InvalidArgumentException {
        String source = "\"a100\".substring(\"a\".length)";
        System.out.println("Source: " + source);
        InstructionSet instructionSet = ExpressionParser.compile(source, CompileData.builder().addVariable("counter", PerformanceCounter.class).build());
        System.out.println("Instructions:");
        instructionSet.forEach(i -> {
            if (!(i instanceof DummyInstruction)) System.out.println("- " + i);
        });
        Object obj = instructionSet.execute(RuntimeData.builder().addVariable("counter", new PerformanceCounter(PerformanceCounter.Unit.MILLISECONDS)).build());
        assert "100".equals(obj) : "expected 100 but got " + obj;
        System.out.println(ExpressionParser.compile("\"\".intern().concat(\"test\").substring(1, 1).substring(1, 1).length", CompileData.builder().build()));
        System.out.println(ExpressionParser.compile("s.concat(s.concat(s))", CompileData.builder().addVariable("s", String.class).build()));
    }
}
