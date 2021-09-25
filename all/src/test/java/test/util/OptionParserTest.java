package test.util;

import org.junit.Test;
import util.option.AcceptingOption;
import util.option.OptionParser;
import util.platform.OSType;

public class OptionParserTest {
    @Test
    public void accept() {
        OptionParser parser = new OptionParser();
        AcceptingOption<OSType> parser1 = parser.accepts("os").withRequiredArg().required().ofType(OSType::valueOf);
        assert parser.parse("os=Windows").value(parser1) == OSType.Windows;
    }

    @Test(expected = RuntimeException.class)
    public void failAccept() {
        OptionParser parser = new OptionParser();
        AcceptingOption<OSType> parser1 = parser.accepts("os").withRequiredArg().required().ofType(OSType::valueOf);
        assert parser.parse().value(parser1) == OSType.Windows; // it will fail on OptionParser#parse, since 'os' is required but not defined
    }

    @Test
    public void defaultValueTest() {
        OptionParser parser = new OptionParser();
        AcceptingOption<OSType> parser1 = parser.accepts("os").withRequiredArg().defaultsTo("Linux").ofType(OSType::valueOf);
        assert parser.parse().value(parser1) == OSType.Linux : parser.parse().value(parser1);
    }
}
