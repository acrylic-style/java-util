package util.option;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ArgumentParser;
import util.Validate;
import util.base.Lists;
import util.function.StringConverter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionParser {
    private final List<AcceptingOption<?>> options = new ArrayList<>();

    @NotNull
    public AcceptingOption<String> accepts(@NotNull String key) {
        Validate.notNull(key, "key cannot be null");
        AcceptingOption<String> option = new AcceptingOption<>(key, StringConverter.identity());
        options.add(option);
        return option;
    }

    @NotNull
    public AcceptingOption<String> accepts(@NotNull String key, @Nullable String description) {
        return accepts(key).description(description);
    }

    @NotNull
    public OptionParserResult parse(@NotNull String@NotNull... args) { return this.parse(Lists.join(Arrays.asList(args), " ")); }

    @NotNull
    public OptionParserResult parse(@NotNull String arg) {
        OptionParserResult result = new OptionParserResult(new ArgumentParser(arg));
        options.stream().filter(Option::isRequired).forEach(option -> {
            String description = option.description == null ? "" : " (" + option.description + ")";
            if (option.isRequiredArg()) {
                if (result.hasNoKey(option.key)) throw new RuntimeException("Option with required arg '" + option.key + "' does not exist!" + description);
            } else {
                if (!result.has(option.key)) throw new RuntimeException("Option '" + option.key + "' does not exist!" + description);
            }
        });
        return result;
    }
    
    @NotNull
    public OptionParser printHelpOn(@NotNull PrintStream out) {
        if (options.stream().anyMatch(Option::isRequired)) {
            out.println("Required options:");
        }
        options.stream().filter(Option::isRequired).forEach(option -> {
            String o = "    --" + option.getKey();
            String def = option.getDefaultValue() == null ? null : "        Default Value: " + option.getDefaultValue();
            if (option.isRequiredArg()) o += "=[value]";
            out.println(o + " (Required)");
            if (option.getDescription() != null) out.println("        " + option.getDescription());
            if (def != null) out.println(def);
        });
        if (options.stream().anyMatch(Option::isNotRequired)) {
            out.println("Optional options:");
        }
        options.stream().filter(Option::isNotRequired).forEach(option -> {
            String o = "    --" + option.getKey();
            String def = option.getDefaultValue() == null ? null : "        Default Value: " + option.getDefaultValue();
            if (option.isRequiredArg()) o += "=[value]";
            out.println(o);
            if (option.getDescription() != null) out.println("        " + option.getDescription());
            if (def != null) out.println(def);
        });
        return this;
    }
}
