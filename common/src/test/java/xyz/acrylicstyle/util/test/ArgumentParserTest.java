package xyz.acrylicstyle.util.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.acrylicstyle.util.ArgumentParsedResult;
import xyz.acrylicstyle.util.ArgumentParser;
import xyz.acrylicstyle.util.ArgumentParserBuilder;
import xyz.acrylicstyle.util.InvalidArgumentException;

import java.util.Arrays;

public class ArgumentParserTest {
    @Test
    public void testSimple() throws InvalidArgumentException {
        ArgumentParsedResult result = ArgumentParser.create().parse("--test=abc -abc=def --abc= --abc a=s if ");
        //System.err.println(result);
        assert result.getArgumentOrThrow("test").equals("abc") : result.getArgumentOrThrow("test");
        assert result.getArgumentOrThrow("abc").isEmpty() : result.getArgumentOrThrow("abc");
        assert result.shortArguments().containsAll(Arrays.asList('a', 'b', 'c', '=', 'd', 'e', 'f')) : result.shortArguments();
        assert result.containsUnhandledArgument("abc") : result.unhandledArguments();
        assert result.containsUnhandledArgument("a=s") : result;
        assert result.containsUnhandledArgument("if") : result.unhandledArguments();

        // check toString output
        InvalidArgumentException ex = Assertions.assertThrows(InvalidArgumentException.class, () -> {
            //noinspection ResultOfMethodCallIgnored
            ArgumentParser.create().parse("--dupe-key=1 --dupe-key=2");
        });
        String exMessage = InvalidArgumentException.class.getTypeName() + ": Duplicate key: dupe-key (Set allowDuplicateKey to true to overwrite the value)\n" +
                "--dupe-key=1 --dupe-key=2\n" +
                "               ^~~~~~~~";
        assert ex.toString().equals(exMessage) : ex.toString();

        // check allowDuplicateKey works as expected
        assert ArgumentParserBuilder.builder()
                .allowDuplicateKey()
                .create()
                .parse("--dupe-key=1 --dupe-key=2")
                .getArgumentOrThrow("dupe-key")
                .equals("2");

        result = ArgumentParserBuilder.builder()
                .parseOptionsWithoutDash()
                .create()
                .parse(" a=s if ");
        assert result.getArgumentOrThrow("a").equals("s") : result;
        assert result.containsUnhandledArgument("if") : result.unhandledArguments();
    }

    @Test
    public void testQuotedAndEscaped() throws InvalidArgumentException {
        // - allow quoted key
        // - allow quoted value and escaped quotes and backslashes
        // - allow quoted unhandled argument (and don't parse as argument)
        ArgumentParsedResult result = ArgumentParser.create().parse("--\" test\"=\"--a=\\\"\\\\\" --\"this=should=be=unhandled=argument\""); // --" test"="--a=\"\\" --"this=should=be=unhandled=argument"
        //System.err.println(result);
        assert result.getArgumentOrThrow(" test").equals("--a=\"\\") : result.getArgumentOrThrow("test");
        assert result.containsUnhandledArgument("this=should=be=unhandled=argument");

        result = ArgumentParserBuilder.builder().literalBackslash().create().parse("--\"\\\"=\"\\n\"");
        assert result.getArgumentOrThrow("\\").equals("\\n") : result;

        InvalidArgumentException ex = Assertions.assertThrows(InvalidArgumentException.class, () -> {
            //noinspection ResultOfMethodCallIgnored
            ArgumentParserBuilder.builder().disallowEscapedBackslash().create().parse("--\" test\"=\"--a=\\\"\\\\\"");
        });
        String exMessage = InvalidArgumentException.class.getTypeName() + ": Invalid escape sequence: '\\\\'\n" +
                "\" test\"=\"--a=\\\"\\\\\"\n" +
                "               ^~";
        assert ex.toString().equals(exMessage) : ex.toString();
    }
}
