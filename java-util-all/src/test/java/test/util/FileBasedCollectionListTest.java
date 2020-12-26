package test.util;

import org.junit.Test;
import util.CollectionList;
import util.ICollectionList;
import util.file.FileBasedCollectionList;

import java.util.Objects;

public class FileBasedCollectionListTest {
    @SuppressWarnings("StringBufferReplaceableByString")
    public String getAssertionErrorMessage(ICollectionList<?> list) {
        StringBuilder str = new StringBuilder();
        str.append("List size: ").append(list.size()).append(", Entries: ").append(list.join());
        return str.toString();
    }

    @Test
    public void join() {
        FileBasedCollectionList<String> list = new FileBasedCollectionList<>();
        list.add("Horse");
        list.add("Cow");
        list.add("Pig");
        assert list.join(", ").equals("Horse, Cow, Pig") : getAssertionErrorMessage(list);
    }

    @Test
    public void map() {
        FileBasedCollectionList<String> list = new FileBasedCollectionList<>("Cow", "Cave", "Cop", "Cape", "Alpha", "Artifact");
        assert list.filter(s -> s.startsWith("C")).size() == 4 : getAssertionErrorMessage(list);
    }

    @Test
    public void concat() {
        FileBasedCollectionList<String> list1 = new FileBasedCollectionList<>("Water");
        FileBasedCollectionList<String> list2 = new FileBasedCollectionList<>("Fire");
        FileBasedCollectionList<String> list3 = new FileBasedCollectionList<>("Wind");
        ICollectionList<String> list4 = list1.thenAddAll(list2).thenAddAll(list3);
        assert list4.size() == 3 : "Expected size 3, but got " + getAssertionErrorMessage(list4);
    }

    @Test
    public void of() {
        FileBasedCollectionList<String> list = FileBasedCollectionList.of("A", "B", "C");
        assert list.size() == 3 : getAssertionErrorMessage(list);
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
        FileBasedCollectionList<String> list = new FileBasedCollectionList<>("u", "n", "i", "q", "u", "e");
        assert list.unique().containsAll(new FileBasedCollectionList<>("q", "u", "e", "i", "n")) : getAssertionErrorMessage(list.unique());
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
