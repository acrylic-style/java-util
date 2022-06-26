package xyz.acrylicstyle.util.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.ArgumentParser;
import xyz.acrylicstyle.util.ArgumentParserBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ArgumentParserBuilderImpl implements ArgumentParserBuilder {
    private static final Set<Character> DEFAULT_ALLOWED_ESCAPED_CHARACTERS =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList('\n', '\r', '\\', '\"', '\t')));

    boolean allowDuplicateKey = false;
    @Nullable Set<@NotNull Character> allowedEscapedCharacters = null;
    boolean literalBackslash = false;

    @Override
    public @NotNull ArgumentParserBuilder allowDuplicateKey() {
        allowDuplicateKey = true;
        return this;
    }

    @Override
    public @NotNull ArgumentParserBuilder disallowEscapedLineTerminators() {
        if (allowedEscapedCharacters == null) {
            allowedEscapedCharacters = new HashSet<>(DEFAULT_ALLOWED_ESCAPED_CHARACTERS);
        }
        allowedEscapedCharacters.remove('\n');
        allowedEscapedCharacters.remove('\r');
        return this;
    }

    @Override
    public @NotNull ArgumentParserBuilder disallowEscapedTabCharacter() {
        if (allowedEscapedCharacters == null) {
            allowedEscapedCharacters = new HashSet<>(DEFAULT_ALLOWED_ESCAPED_CHARACTERS);
        }
        allowedEscapedCharacters.remove('\t');
        return this;
    }

    @Override
    public @NotNull ArgumentParserBuilder disallowEscapedBackslash() {
        if (allowedEscapedCharacters == null) {
            allowedEscapedCharacters = new HashSet<>(DEFAULT_ALLOWED_ESCAPED_CHARACTERS);
        }
        allowedEscapedCharacters.remove('\\');
        return this;
    }

    @Override
    public @NotNull ArgumentParserBuilder disallowEscapedQuote() {
        if (allowedEscapedCharacters == null) {
            allowedEscapedCharacters = new HashSet<>(DEFAULT_ALLOWED_ESCAPED_CHARACTERS);
        }
        allowedEscapedCharacters.remove('"');
        return this;
    }

    @Override
    public @NotNull ArgumentParserBuilder allowedEscapeSequences(char @Nullable ... allowedEscapes) {
        if (allowedEscapes == null) {
            allowedEscapedCharacters = null;
        } else {
            allowedEscapedCharacters = new HashSet<>();
            for (char c : allowedEscapes) {
                allowedEscapedCharacters.add(c);
            }
        }
        return this;
    }

    @Override
    public @NotNull ArgumentParserBuilder literalBackslash() {
        literalBackslash = true;
        return this;
    }

    @Override
    public @NotNull ArgumentParser create() {
        return new ArgumentParserImpl(this);
    }
}
