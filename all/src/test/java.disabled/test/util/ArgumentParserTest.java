package test.util;

import org.junit.Test;
import util.ArgumentParser;

public class ArgumentParserTest {
    @Test
    public void options() {
        ArgumentParser parser = new ArgumentParser("--help=yes");
        assert parser.containsKey("help") && parser.getString("help").equals("yes");
    }

    @Test
    public void intOptions() {
        ArgumentParser parser = new ArgumentParser("--kouta=1212");
        assert parser.containsKey("kouta") && parser.getInt("kouta") == 1212;
    }

    @Test
    public void booleanOptions() {
        ArgumentParser parser = new ArgumentParser("--option=true --option2=false");
        assert parser.containsKey("option") && parser.getBoolean("option");
        assert parser.containsKey("option2") && !parser.getBoolean("option2");
        assert !parser.getBoolean("non-existent-key");
    }

    @Test
    public void literalTest() {
        ArgumentParser parser = new ArgumentParser("--path=\"/dev/null /dev/random\"");
        assert parser.containsKey("path") && parser.getString("path").equals("/dev/null /dev/random") : parser.getString("path");
    }

    @Test
    public void weirdLiteralTest() {
        ArgumentParser parser = new ArgumentParser("--special=\"=!%&!()\\\"\"");
        assert parser.containsKey("special") && parser.getString("special").equals("=!%&!()\"") : parser.getString("special");
    }

    @Test
    public void arguments() {
        ArgumentParser parser = new ArgumentParser("--special \"test\"");
        assert parser.contains("special") && parser.contains("test");
    }

    @Test
    public void simpleArguments() {
        ArgumentParser parser = new ArgumentParser("1 2 3 \"a b c\"");
        assert parser.contains("1") : "didn't contain 1";
        assert parser.contains("2") : "didn't contain 2";
        assert parser.contains("3") : "didn't contain 3";
        assert parser.contains("a b c") : "didn't contain 'a b c'";
    }

    @Test
    public void arguments2() {
        ArgumentParser parser = new ArgumentParser("--help aaa -a");
        assert parser.contains("help") : "didn't contain help";
        assert parser.contains("aaa") : "didn't contain aaa";
        assert parser.contains("-a") : "didn't contain -a";
    }
}
