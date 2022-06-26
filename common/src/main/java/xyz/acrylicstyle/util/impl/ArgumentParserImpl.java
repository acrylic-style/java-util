package xyz.acrylicstyle.util.impl;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.ArgumentParsedResult;
import xyz.acrylicstyle.util.ArgumentParser;
import xyz.acrylicstyle.util.InvalidArgumentException;
import xyz.acrylicstyle.util.StringReader;

import java.util.Set;

public class ArgumentParserImpl implements ArgumentParser {
    private final boolean allowDuplicateKey;
    private final char @Nullable [] allowedEscapedCharacters;
    private final boolean literalBackslash;
    private final boolean parseOptionsWithoutDash;

    public ArgumentParserImpl() {
        this(new ArgumentParserBuilderImpl());
    }

    public ArgumentParserImpl(@NotNull ArgumentParserBuilderImpl builder) {
        this.allowDuplicateKey = builder.allowDuplicateKey;
        this.allowedEscapedCharacters = toCharArray(builder.allowedEscapedCharacters);
        this.literalBackslash = builder.literalBackslash;
        this.parseOptionsWithoutDash = builder.parseOptionsWithoutDash;
    }

    @Override
    public @NotNull ArgumentParsedResult parse(@NotNull StringReader reader) throws InvalidArgumentException {
        ArgumentParsedResultImpl.Builder builder = new ArgumentParsedResultImpl.Builder();
        while (reader.hasNext()) {
            // we don't care about whitespaces and line terminators here
            reader.skipWhitespace();
            reader.skipLineTerminators();
            if (reader.isEOF()) {
                break;
            }
            char c = reader.read();
            if (c == '-') {
                if (reader.peek() == '-') {
                    // long argument (--)
                    reader.skip();
                    if (reader.isEOF() || reader.skipWhitespace() >= 1 || reader.skipLineTerminators() >= 1) {
                        builder.addUnhandled("--");
                        continue;
                    }
                    parseOption(builder, reader);
                } else {
                    // short argument (-)
                    if (reader.isEOF() || reader.skipWhitespace() >= 1 || reader.skipLineTerminators() >= 1) {
                        builder.addUnhandled("-");
                        continue;
                    }
                    String s = reader.readToken();
                    for (char c1 : s.toCharArray()) {
                        builder.addShort(c1);
                    }
                }
            } else if (parseOptionsWithoutDash) {
                reader.skip(-1);
                parseOption(builder, reader);
            } else {
                builder.addUnhandled(c + reader.readToken());
            }
        }
        return builder.build();
    }

    private void parseOption(@NotNull ArgumentParsedResultImpl.Builder builder, @NotNull StringReader reader) throws InvalidArgumentException {
        String key;
        int read;
        if (reader.peek() == '\"') {
            key = reader.readQuotedString(literalBackslash, allowedEscapedCharacters);
            read = key.length() + 2;
        } else {
            key = reader.readUntil('=', ' ', '\n', '\r');
            read = key.length();
        }
        if (reader.isEOF() || reader.skipWhitespace() >= 1 || reader.skipLineTerminators() >= 1) {
            builder.addUnhandled(key);
            return;
        }
        if (!allowDuplicateKey && builder.containsKey(key)) {
            throw new InvalidArgumentException("Duplicate key: " + key + " (Set allowDuplicateKey to true to overwrite the value)")
                    .withContext(reader, -read, read);
        }
        if (reader.peek() == '=') {
            reader.skip();
            String value = reader.readQuotableString(literalBackslash, allowedEscapedCharacters);
            builder.put(key, value);
        }
    }

    @Contract("null -> null; !null -> !null")
    private static char[] toCharArray(@Nullable Set<@NotNull Character> characterSet) {
        if (characterSet == null) {
            return null;
        }
        char[] chars = new char[characterSet.size()];
        int i = 0;
        for (char c : characterSet) {
            chars[i++] = c;
        }
        return chars;
    }
}
