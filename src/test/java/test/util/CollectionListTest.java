package test.util;

import org.junit.Test;
import util.CollectionList;

public class CollectionListTest {
    @SuppressWarnings("StringBufferReplaceableByString")
    public String getAssertionErrorMessage(CollectionList<?> list) {
        StringBuilder str = new StringBuilder();
        str.append("List size: ").append(list.size()).append(", Entries").append(list.join());
        return str.toString();
    }

    @Test
    public void join() {
        CollectionList<String> list = new CollectionList<>("Horse", "Cow", "Pig");
        assert list.join(", ").equals("Horse, Cow, Pig") : getAssertionErrorMessage(list);
    }

    @Test
    public void map() {
        CollectionList<String> list = new CollectionList<>("Cow", "Cave", "Cop", "Cape", "Alpha", "Artifact");
        assert list.filter(s -> s.startsWith("C")).size() == 4 : getAssertionErrorMessage(list);
    }

    @Test
    public void concat() {
        CollectionList<String> list1 = new CollectionList<>("Water");
        CollectionList<String> list2 = new CollectionList<>("Fire");
        CollectionList<String> list3 = new CollectionList<>("Wind");
        CollectionList<String> list4 = list1.concat(list2, list3);
        assert list4.size() == 3 : getAssertionErrorMessage(list4);
    }

    @Test
    public void of() {
        CollectionList<String> list = CollectionList.of("A", "B", "C");
        assert list.size() == 3 : getAssertionErrorMessage(list);
    }

    @Test
    public void shift() {
        CollectionList<String> list = new CollectionList<>("A", "B", "C");
        list.shift();
        assert !list.contains("A") && list.contains("B") && list.contains("C") : getAssertionErrorMessage(list);
    }

    @Test
    public void unshift() {
        CollectionList<String> list = new CollectionList<>("D", "E", "F");
        list.unshift("A", "B", "C");
        assert list.contains("A")
                && list.contains("B")
                && list.contains("C")
                && list.contains("D")
                && list.contains("E")
                && list.contains("F")
                : getAssertionErrorMessage(list);
    }
}
