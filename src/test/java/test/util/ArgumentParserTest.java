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
    }

    @Test
    public void literalTest() {
        ArgumentParser parser = new ArgumentParser("--path=\"/dev/null /dev/random\"");
        assert parser.containsKey("path") && parser.getString("path").equals("/dev/null /dev/random") : parser.getString("path");
    }

    @Test
    public void weirdLiteralTest() {
        ArgumentParser parser = new ArgumentParser("--special=\"=!%&!()\"");
        assert parser.containsKey("special") && parser.getString("special").equals("=!%&!()") : parser.getString("special");
    }

    @Test
    public void arguments() {
        ArgumentParser parser = new ArgumentParser("--special \"test\"");
        assert parser.contains("special") && parser.contains("test");
    }
}
