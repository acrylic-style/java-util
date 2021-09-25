package test.util;

import org.junit.Test;
import util.experimental.GeneratorFunction;

import static util.javascript.JavaScript.If;
import static util.javascript.JavaScript.parseInt;

public class JavaScriptTest {
    @Test
    public void parseIntTest() {
        assert parseInt("1_000") == 1_000;
    }

    @Test
    public void parseIntHexTest() {
        assert parseInt("ff", 16) == 255 : "expected 255, but got: " + parseInt("ff", 16);
    }

    @Test
    public void ifTest() {
        assert If(new Object());
        assert If(1);
        assert !If(0);
        assert If(" ");
        assert If("a");
        assert !If("");
    }

    @Test
    public void notConvertibleParseIntTest() {
        assert parseInt("java-util is the best") == 0;
    }

    @Test
    public void generatorFunction() {
        GeneratorFunction<Object> generatorFunction = function -> {
            function.yield(0);
            function.yield(1);
            function.yield(2);
            function.yield("Hello o/");
            return "hi";
        };
        assert (int) generatorFunction.next().value == 0;
        assert (int) generatorFunction.next().value == 1;
        assert (int) generatorFunction.next().value == 2;
        assert generatorFunction.next().value.equals("Hello o/");
        assert generatorFunction.next().value.equals("hi");
        assert !generatorFunction.hasNext();
    }
}
