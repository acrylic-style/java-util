package util.base;

import org.jetbrains.annotations.NotNull;
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

    public static int levenshteinDistance(@NotNull String a, @NotNull String b) {
        return levenshteinDistance(a, b, Integer.MAX_VALUE);
    }

    public static int levenshteinDistance(@NotNull String a, @NotNull String b, int limit) {
        if (a.length() == 0) return b.length();
        if (b.length() == 0) return a.length();
        if (a.equals(b)) return 0;
        int[] a1 = new int[b.length() + 1];
        int[] a2 = new int[b.length() + 1];
        int[] temp;
        for (int i = 0; i < a1.length; i++) {
            a1[i] = i;
        }
        for (int i = 0; i < a.length(); i++) {
            a2[0] = i + 1;
            int min = a2[0];
            for (int i1 = 0; i1 < b.length(); i1++) {
                int cost = 1;
                if (a.charAt(i) == b.charAt(i1)) {
                    cost = 0;
                }
                a2[i1 + 1] = MathUtils.min(
                        a2[i1] + 1,
                        a1[i1 + 1] + 1,
                        a1[i1] + cost
                );
                min = Math.min(min, a2[i1 + 1]);
            }
            if (min >= limit) {
                return limit;
            }
            temp = a1;
            a1 = a2;
            a2 = temp;
        }
        return a1[b.length()];
    }

    @NotNull
    public static String charAt(@NotNull String s, int index) {
        if (index < 0) return "";
        return String.valueOf(s.charAt(index));
    }
}
