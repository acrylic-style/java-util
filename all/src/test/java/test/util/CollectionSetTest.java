package test.util;

import org.junit.Test;
import util.CollectionList;
import util.CollectionSet;
import util.ICollectionList;

import java.util.Objects;

public class CollectionSetTest {
    @SuppressWarnings("StringBufferReplaceableByString")
    public String getAssertionErrorMessage(ICollectionList<?> list) {
        StringBuilder str = new StringBuilder();
        str.append("List size: ").append(list.size()).append(", Entries: ").append(list.join());
        return str.toString();
    }

    @Test
    public void join() {
        CollectionSet<String> list = new CollectionSet<>("Horse", "Cow", "Pig");
        assert list.join(", ").equals("Horse, Cow, Pig") : getAssertionErrorMessage(list);
    }

    @Test
    public void map() {
        CollectionSet<String> list = new CollectionSet<>("Cow", "Cave", "Cop", "Cow", "Cape", "Alpha", "Artifact");
        assert list.filter(s -> s.startsWith("C")).size() == 4 : getAssertionErrorMessage(list);
    }

    @Test
    public void concat() {
        CollectionSet<String> list1 = new CollectionSet<>("Water");
        CollectionSet<String> list2 = new CollectionSet<>("Fire");
        CollectionSet<String> list3 = new CollectionSet<>("Wind");
        ICollectionList<String> list4 = list1.thenAddAll(list2).thenAddAll(list3);
        assert list4.size() == 3 : "Expected size 3, but got " + getAssertionErrorMessage(list4);
    }

    @Test
    public void of() {
        CollectionSet<String> list = CollectionSet.of("A", "B", "C");
        assert list.size() == 3 : getAssertionErrorMessage(list);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Test
    public void equals() {
        CollectionSet<String> list1 = new CollectionSet<>("l", "i", "s", "t");
        CollectionSet<String> list2 = new CollectionSet<>("l", "i", "s", "t");
        assert list1.equals(list2) && list2.equals(list1);
    }

    @Test
    public void unique() {
        CollectionSet<String> list = new CollectionSet<>("u", "n", "i", "q", "u", "e");
        assert list.containsAll(new CollectionSet<>("q", "u", "e", "i", "n")) : getAssertionErrorMessage(list.unique());
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
