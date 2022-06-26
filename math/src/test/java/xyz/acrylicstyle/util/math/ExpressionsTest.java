package xyz.acrylicstyle.util.math;

import org.junit.jupiter.api.Test;
import xyz.acrylicstyle.util.InvalidArgumentException;

public class ExpressionsTest {
    //@Test
    public void testSimple() throws InvalidArgumentException {
        assert ExpressionParser.parse("123").evaluate().intValue() == 123;
        assert ExpressionParser.parse(" ( 123 ) + (123)").evaluate().intValue() == 123;
    }
}
