package util.yaml;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;
import util.Validate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class YamlConfiguration {
    private final Yaml yaml = new Yaml();
    private final InputStream inputStream;

    public YamlConfiguration(@NotNull String path) throws FileNotFoundException { this(new File(path)); }

    public YamlConfiguration(@NotNull File file) throws FileNotFoundException {
        Validate.notNull(file, "file cannot be null");
        this.inputStream = new FileInputStream(file);
    }

    public YamlConfiguration(@NotNull InputStream inputStream) {
        Validate.notNull(inputStream, "inputStream cannot be null");
        this.inputStream = inputStream;
    }

    @NotNull
    public YamlObject asObject() { return new YamlObject(yaml.load(inputStream)); }

    @NotNull
    public YamlArray asArray() { return new YamlArray(yaml.load(inputStream)); }
}
