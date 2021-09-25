package test.util;

import org.junit.Test;
import util.ResourceLocator;

public class ResourceLocatorTest {
    @Test
    public void getResource() {
        ResourceLocator resourceLocator = ResourceLocator.getInstance(ResourceLocatorTest.class);
        assert resourceLocator.getResource("/test.yml") != null;
        assert resourceLocator.getResource("./test.yml") != null;
        assert resourceLocator.getResource("test.yml") != null;
        assert resourceLocator.getResource("nope.yml") == null;
    }
}
