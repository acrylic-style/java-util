package util.argument;

import org.jetbrains.annotations.NotNull;
import util.StringReader;

class ArgumentParser {
    public ArgumentParser() {
    }

    public static ArgumentParser parse(@NotNull String args) {
        return parse(new StringReader(args));
    }

    public static ArgumentParser parse(@NotNull StringReader reader) {
        return new ArgumentParser();
    }
}
