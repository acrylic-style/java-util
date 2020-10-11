package test.util;

import org.junit.Test;
import util.yaml.YamlConfiguration;
import util.yaml.YamlObject;

import java.util.Objects;

public class YamlTest {
    @Test
    public void test() {
        YamlConfiguration config = new YamlConfiguration(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("test.yml")));
        YamlObject obj = config.asObject().getObject("aa").getObject("bb").getObject("cc");
        assert obj.getString("dd").equals("ee");
        assert obj.getInt("ff") == 1;
        assert obj.getBoolean("gg");
        assert obj.getString("non-existent-value", "yes").equals("yes");
    }
}
