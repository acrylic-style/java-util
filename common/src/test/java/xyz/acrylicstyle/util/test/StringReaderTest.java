package xyz.acrylicstyle.util.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.acrylicstyle.util.InvalidArgumentException;
import xyz.acrylicstyle.util.StringReader;

public class StringReaderTest {
    @Test
    public void testUnquoted() {
        StringReader reader = StringReader.create("abc");
        assert reader.readToken().equals("abc");
    }

    @Test
    public void testQuoted() throws InvalidArgumentException {
        StringReader reader = StringReader.create("\"abc\"");
        assert reader.readQuotedString().equals("abc");
        InvalidArgumentException ex = Assertions.assertThrows(InvalidArgumentException.class, () -> {
            StringReader reader2 = StringReader.create("\"abc");
            reader2.readQuotedString();
        });
        String exMessage = InvalidArgumentException.class.getTypeName() + ": Unexpected EOF while looking for '\"'\n" +
                "\"abc\n" +
                "    ^";
        assert ex.toString().equals(exMessage) : ex.toString();
    }

    @Test
    public void testEscapes() throws InvalidArgumentException {
        StringReader reader = StringReader.create("\"abc\\n\\\"def\\\"\"");
        String s = reader.readQuotableString();
        assert s.equals("abc\n\"def\"") : s;
    }
}
