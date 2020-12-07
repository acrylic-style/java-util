package test.util;

import org.junit.Test;

import static util.MathUtils.*;

public class MathUtilsTest {
    @Test
    public void min3() {
        assert min(5, 1, 6) == 1 : min(5, 1, 6);
    }

    @Test
    public void min4() {
        assert min(9, 7, 2, 5) == 2 : min(9, 7, 2, 5);
    }

    @Test
    public void minWithNull() {
        assert min(999999, null, 555) == 555 : min(999999, null, 5);
    }

    @Test
    public void parseHexTest() {
        assert parseHex("0xFF") == 255 : parseHex("0xFF");
    }
}
