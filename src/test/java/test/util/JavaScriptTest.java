package test.util;

import org.junit.Test;
import util.javascript.GeneratorFunction;

import java.util.function.Consumer;

import static util.javascript.JavaScript.*;

public class JavaScriptTest {
    @Test
    public void parseIntTest() {
        assert parseInt("1_000") == 1_000;
    }

    @Test
    public void parseIntHexTest() {
        assert parseInt("ff", 16) == 0xff : "Parsed result was " + parseInt("ff", 16); // 256
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
        assert parseInt("YouTube") == 0;
    }

    @Test
    public void generatorFunction() {
        GeneratorFunction generatorFunction = new GeneratorFunction() {
            @Override
            public void apply(Consumer<Object> yield, Object... o) {
                yield.accept(0);
                yield.accept(1);
                yield.accept(2);
                yield.accept("Hello o/");
            }
        };
        assert (int) generatorFunction.next() == 0;
        assert (int) generatorFunction.next() == 1;
        assert (int) generatorFunction.next() == 2;
        assert generatorFunction.next().equals("Hello o/");
    }
}
