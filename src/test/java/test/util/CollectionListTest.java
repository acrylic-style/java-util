package test.util;

import org.junit.Test;
import util.CollectionList;

import java.util.Objects;

public class CollectionListTest {
    @SuppressWarnings("StringBufferReplaceableByString")
    public String getAssertionErrorMessage(CollectionList<?> list) {
        StringBuilder str = new StringBuilder();
        str.append("List size: ").append(list.size()).append(", Entries: ").append(list.join());
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
        assert list4.size() == 3 : "Expected size 3, but got " + getAssertionErrorMessage(list4);
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

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Test
    public void equals() {
        CollectionList<String> list1 = new CollectionList<>("l", "i", "s", "t");
        CollectionList<String> list2 = new CollectionList<>("l", "i", "s", "t");
        assert list1.equals(list2) && list2.equals(list1);
    }

    @Test
    public void unique() {
        CollectionList<String> list = new CollectionList<>("u", "n", "i", "q", "u", "e");
        assert list.unique().containsAll(new CollectionList<>("q", "u", "e", "i", "n")) : getAssertionErrorMessage(list.unique());
    }

    @Test
    public void first() {
        assert Objects.equals(new CollectionList<>("a", "b", "c").first(), "a");
    }

    @Test
    public void last() {
        assert Objects.equals(new CollectionList<>("a", "b", "c").last(), "c");
    }
}
