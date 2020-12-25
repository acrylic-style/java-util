package util.base;

import org.jetbrains.annotations.NotNull;
import util.CollectionList;
import util.MathUtils;
import util.Validate;

public class Strings {
    @NotNull
    public static String repeat(@NotNull String s, int times) {
        Validate.notNull(s, "string cannot be null");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < times; i++) result.append(s);
        return result.toString();
    }

    /* // Broken thing
    public static int levenshteinDistance(@NotNull String a, @NotNull String b) {
        if (a.length() == 0) return b.length();
        if (b.length() == 0) return a.length();
        CollectionList<Integer> row = new CollectionList<>();
        for (int i = 0; i < a.length(); i++) row.add(i);
        int prev;
        for (int i = 0; i < b.length(); i++) {
            prev = i;
            for (int j = 0; j < a.length(); j++) {
                int val;
                if (charAt(b, i - 1).equals(charAt(a, j - 1))) {
                    val = row.get(Math.max(j - 1, 0));
                } else {
                    val = MathUtils.min((j - 1 < 0 ? null : row.get(j - 1) + 1),
                            prev + 1,
                            row.get(j) + 1);
                }
                row.set(Math.max(j - 1, 0), prev);
                prev = val;
            }
            row.set(a.length(), prev);
        }
        return row.get(a.length());
    }
    */

    @NotNull
    public static String charAt(@NotNull String s, int index) {
        if (index < 0) return "";
        return String.valueOf(s.charAt(index));
    }
}
