package test.util;

import org.junit.Test;
import util.base.Strings;

public class StringsTest {
    @Test
    public void repeat() {
        int times = 5;
        String s = "a";
        assert Strings.repeat(s, times).equals("aaaaa") : "expected aaaaa, but got: " + Strings.repeat(s, times);
    }

    /*
    @Test
    public void levenshtein() {
        assert Strings.levenshteinDistance("abcdef", "abcdeg") == 1 : "expected 1, but got: " + Strings.levenshteinDistance("abcdef", "abcdeg");
        assert Strings.levenshteinDistance("abcde", "abcdef") == 1 : "expected 1, but got: " + Strings.levenshteinDistance("abcde", "abcdef");
        assert Strings.levenshteinDistance("baddogblahblah", "gooddogblahblah") == 4 : "expected 4, but got: " + Strings.levenshteinDistance("baddogblahblah", "gooddogblahblah");
        assert Strings.levenshteinDistance("notgood", "aaa") == 7 : "expected 7, but got: " + Strings.levenshteinDistance("notgood", "aaa");
    }
    */
}
