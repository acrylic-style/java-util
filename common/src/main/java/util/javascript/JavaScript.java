package util.javascript;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents some javascript global contexts.
 */
@Deprecated
public final class JavaScript {
    private JavaScript() {}

    @Contract(value = "null -> false", pure = true)
    public static boolean If(Object o) {
        if (o == null) return false;
        if (o instanceof Boolean) {
            return (boolean) o;
        }
        if (o instanceof Integer) {
            return (int) o != 0;
        }
        if (o instanceof Float) {
            float f = (float) o;
            return !Float.isNaN(f) && f != 0.0F;
        }
        if (o instanceof Double) {
            double d = (double) o;
            return !Double.isNaN(d) && d == 0.0F;
        }
        if (o instanceof String) {
            return !((String) o).isEmpty();
        }
        return true;
    }

    /**
     * The <b>parseInt()</b> function parses a string argument
     * and returns an integer of the specified radix
     * (the base in mathematical numeral systems).
     * @param o The value to parse. If this argument is not a string,
     *          then it is converted to one using the toString abstract
     *          operation. All underscores and whitespaces will be ignored.
     * @param radix An integer between 2 and 36 that represents the radix
     *              (the base in mathematical numeral systems) of the string.
     * @return An integer parsed from the given string.<br>
     * Or 0 when:
     * Couldn't convert object to number.
     */
    public static int parseInt(@NotNull Object o, int radix) {
        try {
            return Integer.parseInt(o.toString().replaceAll("\\s+", "").replaceAll("_", ""), radix);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * The <b>parseInt()</b> function parses a string argument
     * and returns an integer of the specified radix
     * (the base in mathematical numeral systems).
     * @param o The value to parse. If this argument is not a string,
     *          then it is converted to one using the toString abstract
     *          operation. Leading whitespace in this argument is ignored.<br>
     *          All underscores will be replaced with empty string and will be parsed as number.<br>
     *          The default radix is 10.
     * @return An integer parsed from the given string.<br>
     * Or 0 when:
     * Couldn't convert object to number.
     */
    public static int parseInt(Object o) {
        return parseInt(o, 10);
    }
}
