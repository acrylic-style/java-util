package xyz.acrylicstyle.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.impl.ArgumentParserBuilderImpl;

public interface ArgumentParserBuilder {
    /**
     * Allow duplicate key. Default is false. This setting does not affect short arguments and unhandled arguments.
     * @return this
     */
    @Contract("-> this")
    @NotNull
    ArgumentParserBuilder allowDuplicateKey();

    /**
     * Disallow escaped line terminators (<code>\n</code> and <code>\r</code>) in quoted strings.
     * @return this
     */
    @Contract("-> this")
    @NotNull
    ArgumentParserBuilder disallowEscapedLineTerminators();

    /**
     * Disallow escaped tab character (<code>\t</code>) in quoted strings.
     */
    @Contract("-> this")
    @NotNull
    ArgumentParserBuilder disallowEscapedTabCharacter();

    /**
     * Disallow escaped backslash (<code>\\</code>) in quoted strings.
     */
    @Contract("-> this")
    @NotNull
    ArgumentParserBuilder disallowEscapedBackslash();

    /**
     * Disallow escaped quote (<code>\"</code>) in quoted strings.
     */
    @Contract("-> this")
    @NotNull
    ArgumentParserBuilder disallowEscapedQuote();

    /**
     * Allow provided escape characters in quoted strings. This method overrides the {@link #disallowEscapedQuote()},
     * {@link #disallowEscapedBackslash()}, {@link #disallowEscapedLineTerminators()}, and
     * {@link #disallowEscapedTabCharacter()}.
     * @param allowedEscapes set of allowed escape characters. See {@link StringReader#readQuotedString(char...)} for
     *                       valid characters. If null, all escape characters are allowed.
     * @return this
     */
    @Contract("_ -> this")
    @NotNull
    ArgumentParserBuilder allowedEscapeSequences(char @Nullable ... allowedEscapes);

    /**
     * Interprets the bash slash as a literal character in quoted strings. Default is false.
     */
    @Contract("-> this")
    @NotNull
    ArgumentParserBuilder literalBackslash();

    /**
     * Parses arguments without the <code>--</code> prefix. Default is false. If true, the parser will attempt to parse
     * <code>key=value</code> to <code>{key=value}</code> (in arguments). If false, <code>key=value</code> will be put
     * into the unhandled arguments.
     */
    @Contract("-> this")
    @NotNull
    ArgumentParserBuilder parseOptionsWithoutDash();

    /**
     * Create a new instance of {@link ArgumentParser}.
     * @return the instance
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    ArgumentParser create();

    /**
     * Creates a new instance of {@link ArgumentParserBuilder}. The builder is not thread-safe.
     * @return the instance
     */
    @Contract(" -> new")
    static @NotNull ArgumentParserBuilder builder() {
        return new ArgumentParserBuilderImpl();
    }
}
