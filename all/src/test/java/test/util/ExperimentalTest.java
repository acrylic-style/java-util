package test.util;

import org.junit.jupiter.api.Test;
import util.function.generator.GeneratorFunction;
import util.function.generator.GeneratorFunctionResult;

public class ExperimentalTest {
    @Test
    public void generatorFunction() {
        GeneratorFunction<Integer> generatorFunction = function -> {
            int i = 0;
            while (true) {
                if (i == 10) break;
                function.yield(i++);
            }
            return 10;
        };
        assert generatorFunction.next().value == 0;
        assert generatorFunction.next().value == 1;
        assert generatorFunction.next().value == 2;
        assert generatorFunction.next().value == 3;
        assert generatorFunction.next().value == 4;
        assert generatorFunction.next().value == 5;
        assert generatorFunction.next().value == 6;
        assert generatorFunction.next().value == 7;
        assert generatorFunction.next().value == 8;
        assert generatorFunction.next().value == 9;
        GeneratorFunctionResult<Integer> result = generatorFunction.next();
        assert result.value == 10 && result.done;
    }

    @Test
    public void generatorFunction2() {
        int i = 10;
        GeneratorFunction<Integer> generatorFunction = function -> {
            function.yield(i);
            return i + 10;
        };
        assert generatorFunction.next().value == 10;
        assert generatorFunction.next().value == 20;
    }

    /*
    @Test(expected = IllegalStateException.class)
    public void invalidGeneratorFunction() {
        GeneratorFunction<Integer> generatorFunction = function -> 0;
        generatorFunction.yield(0);
    }
    */
}
