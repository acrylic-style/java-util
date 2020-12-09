package test.util;

import org.junit.Test;
import util.yaml.YamlConfiguration;
import util.yaml.YamlObject;

import java.util.Objects;

public class YamlTest {
    @Test
    public void readTest() {
        YamlConfiguration config = new YamlConfiguration(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("test.yml")));
        YamlObject obj = config.asObject().getObject("aa").getObject("bb").getObject("cc");
        assert obj.getString("dd").equals("ee");
        assert obj.getInt("ff") == 1;
        assert obj.getBoolean("gg");
        assert obj.getString("non-existent-value", "yes").equals("yes");
        assert obj.getString("non-existent-value") == null;
    }

    @Test
    public void writeTest() {
        YamlObject object = new YamlObject();
        object.set("abc", "def");
        object.set("def", 123);
        assert object.dump().equals("abc: def\ndef: 123\n") : object.dump();
    }
}
