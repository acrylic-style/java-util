package test.util;

import org.junit.Test;
import util.collection.MultiCollection;

public class MultiCollectionTest {
    public static final MultiCollection<String, String> map = new MultiCollection<>();

    @Test
    public void addAndGet() {
        map.clear();
        map.add("a", "a");
        map.add("a", "b");
        map.add("a", "c");
        assert map.get("a", 1).equals("b") : "Expected 'b', but got " + map.get("a", 1);
    }

    @Test
    public void remove() {
        map.clear();
        map.add("a", "a");
        map.add("a", "b");
        map.add("a", "c");
        map.remove("a", 2);
        assert map.size("a") == 2 : "Expected '2' size, but got " + map.size();
    }

    @Test
    public void isEmpty() {
        map.add("a", "a");
        map.add("a", "a");
        map.add("a", "a");
        map.add("a", "a");
        map.add("a", "a");
        map.clear("a");
        assert map.isEmpty("a");
        map.clear();
        assert map.isEmpty();
    }

    @Test
    public void cloneTest() {
        map.clear();
        map.add("a", "b");
        map.add("a", "b");
        map.add("a", "b");
        map.add("a", "b");
        map.add("a", "b");
        map.add("a", "b");
        map.add("a", "b"); // 7
        assert map.clone().size("a") == 7 : "Expected '7' size, but got " + map.clone().size("a");
    }

    @Test
    public void concat() {
        map.clear();
        map.add("a", "a");
        map.add("a", "b");
    }
}
