package util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple argument parser
 */
public class ArgumentParser {
    public final CollectionList<String> arguments = new CollectionList<>();
    public final StringCollection<Object> parsedOptions = new StringCollection<>();
    public final StringCollection<String> parsedRawOptions = new StringCollection<>();

    /**
     * Parses arguments.<br />
     * Arguments must be in following format:
     * <ul>
     *     <li><code>--help</code> will be parsed as arguments -> help</li>
     *     <li><code>--option=yes</code> will be parsed as parsedOptions -> option:yes (string)</li>
     *     <li><code>--option=true</code> will be parsed as parsedOptions -> option:true (boolean)</li>
     *     <li><code>--"help=true"</code> will be parsed as arguments -> <code>help=true</code></li>
     *     <li><code>"--"</code> will be parsed as arguments -> <code>--</code></li>
     *     <li><code>--help = true</code> will be parsed as arguments -> <code>help, =, yes</code></li>
     * </ul>
     * Please note that multi-byte whitespace will be converted to single-byte whitespace.
     * @param args Arguments
     */
    public ArgumentParser(@NotNull String args) {
        Preconditions.checkNotNull(args, "args cannot be null");
        Matcher matcher = Pattern.compile("\"(.*?)\"").matcher(args);
        while (matcher.find()) {
            args = args.replaceFirst(Pattern.quote(matcher.group()), matcher.group().replaceAll(" ", "　")); // ugly hack to bypass single-space splits
        }
        for (String s : args.split("[ ]+")) { // single-byte whitespace
            s = s.replaceAll("　", " "); // multi-byte whitespace to single-byte whitespace
            s = s.replaceAll("\"(.*)\"", "$1");
            if (Pattern.compile("(\\s+|)\"(.*)\"(\\s+|)").matcher(s).matches()) {
                arguments.add(s.replaceFirst("\"(.*)\"", "$1"));
                continue;
            }
            String args2 = s.replaceFirst("--", "")
                    .replaceAll("(.*)=(.*).*", "$1=$2");
            if (args2.startsWith("\"") || args2.equals("=") || !args2.contains("=")) {
                arguments.add(s.replaceFirst("--", "").replaceFirst("\"(.*)\"", "$1"));
            } else {
                String k = args2.replaceAll("(.*?)=(.*)", "$1");
                String v = args2.replaceAll("(.*?)=(.*)", "$2");
                parsedOptions.add(k, parseString(v));
                parsedRawOptions.add(k, v);
            }
        }
    }

    private Object parseString(@NotNull String s) {
        if (s.equals("true")) return true;
        if (s.equals("false")) return false;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignore) {}
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ignore) {}
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException ignore) {}
        return s;
    }

    /**
     * Checks if option contains key.
     * @param key Key
     */
    public boolean containsKey(@NotNull String key) {
        return parsedOptions.containsKey(key);
    }

    /**
     * @return immutable arguments list
     */
    @NotNull
    public ICollectionList<String> getArguments() {
        return arguments.clone();
    }

    /**
     * Checks if arguments list contains key.
     * @param key Key
     */
    public boolean contains(@NotNull String key) {
        return arguments.contains(key);
    }

    /**
     * Get option with key.
     * @param key Key
     * @return {@link Object}, null if not found
     */
    public Object get(@NotNull String key) {
        return parsedOptions.get(key);
    }

    /**
     * Get string option with key.<br />
     * This method never throws ClassCastException.
     * @param key Key
     * @return {@link String}, null if not found
     */
    public String getString(@NotNull String key) {
        return parsedOptions.containsKey(key) ? parsedOptions.get(key).toString() : null;
    }

    /**
     * Get value by key.
     * @param key Key
     * @return {@link Double}, 0 if not found
     */
    public double getDouble(@NotNull String key) {
        return parsedOptions.containsKey(key) ? (double) parsedOptions.get(key) : 0;
    }

    /**
     * Get value by key.
     * @param key Key
     * @return {@link Float}, 0 if not found
     */
    public float getFloat(@NotNull String key) {
        return parsedOptions.containsKey(key) ? (float) parsedOptions.get(key) : 0F;
    }

    /**
     * Get int option with key.
     * @param key Key
     * @return {@link Integer}, 0 if not found
     * @throws ClassCastException If {@link Object} is other than {@link Integer}.
     */
    public int getInt(@NotNull String key) {
        if (!parsedOptions.containsKey(key)) return 0;
        return (int) parsedOptions.get(key);
    }

    /**
     * Get boolean option with key.
     * @param key Key
     * @return {@link Boolean}
     * @throws ClassCastException If {@link Object} is other than {@link Boolean}.
     */
    public boolean getBoolean(@NotNull String key) {
        if (!parsedOptions.containsKey(key)) return false;
        return (boolean) parsedOptions.get(key);
    }
}
