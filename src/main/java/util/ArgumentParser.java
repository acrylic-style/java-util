package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentParser {
    public CollectionList<String> arguments = new CollectionList<>();
    public StringCollection<Object> parsedOptions = new StringCollection<>();

    /**
     * Parses arguments.<br />
     * Arguments must be in following format:
     * <ul>
     *     <li>'--help' will be parsed as arguments -> help</li>
     *     <li>'--option=yes' will be parsed as parsedOptions -> option:yes (string)</li>
     *     <li>'--option=true' will be parsed as parsedOptions -> option:true (boolean)</li>
     *     <li>'--"help=true"' will be parsed as arguments -> 'help=true'</li>
     *     <li>'"--"' will be parsed as arguments -> '--'</li>
     *     <li>'--help = true' will be parsed as arguments -> help, =, yes</li>
     * </ul>
     * Please note that multi-byte whitespace will be converted to single-byte whitespace.
     * @param args Arguments
     */
    public ArgumentParser(String args) {
        Matcher matcher = Pattern.compile("\".*?\"").matcher(args);
        while (matcher.find()) {
            args = args.replaceAll(matcher.group(), matcher.group().replaceAll(" ", "　"));
        }
        for (String s : args.split("[ ]+")) {
            s = s.replaceAll("　", " ");
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
            }
        }
    }

    private Object parseString(String s) {
        if (s.equals("true")) return true;
        if (s.equals("false")) return false;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignore) {}
        return s;
    }

    /**
     * Checks if option contains key.
     * @param key Key
     */
    public boolean containsKey(String key) {
        return parsedOptions.containsKey(key);
    }

    /**
     * Checks if arguments list contains key.
     * @param key Key
     */
    public boolean contains(String key) {
        return arguments.contains(key);
    }

    /**
     * Get option with key.
     * @param key Key
     * @return {@link Object}, null if not found
     */
    public Object get(String key) {
        return parsedOptions.get(key);
    }

    /**
     * Get string option with key.
     * @param key Key
     * @return {@link String}, null if not found
     * @throws ClassCastException If {@link Object} is other than {@link String}.
     */
    public String getString(String key) {
        return (String) parsedOptions.get(key);
    }

    /**
     * Get int option with key.
     * @param key Key
     * @return {@link Integer}, 0 if not found
     * @throws ClassCastException If {@link Object} is other than {@link Integer}.
     */
    public int getInt(String key) {
        return (int) parsedOptions.get(key);
    }

    /**
     * Get boolean option with key.
     * @param key Key
     * @return {@link Boolean}
     * @throws ClassCastException If {@link Object} is other than {@link Boolean}.
     */
    public boolean getBoolean(String key) {
        return (boolean) parsedOptions.get(key);
    }
}
