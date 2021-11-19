package util.argument.test;

import org.junit.jupiter.api.Test;
import util.StringReader;
import util.argument.InvalidArgumentException;
import util.argument.parser.LiteralParser;

import static org.junit.jupiter.api.Assertions.*;

public class ArgumentParserTest {
    @Test
    public void literalParser() throws InvalidArgumentException {
        assertThrows(InvalidArgumentException.class, () -> LiteralParser.INSTANCE.parse(new StringReader("\"aaaa")));
        assertEquals("literal string, no matter what", LiteralParser.INSTANCE.parse(new StringReader("\"literal string, no matter what\"")));
    }
}
