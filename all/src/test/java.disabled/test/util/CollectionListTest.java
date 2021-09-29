package test.util;

import org.junit.Test;
import util.collection.CollectionList;
import util.collection.ICollectionList;

import java.util.Objects;

public class CollectionListTest {
    @SuppressWarnings("StringBufferReplaceableByString")
    public String getAssertionErrorMessage(ICollectionList<?> list) {
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
        ICollectionList<String> list4 = list1.thenAddAll(list2).thenAddAll(list3);
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
        list.unshift("D");
        assert list.get(0).equals("D")
                && list.get(1).equals("A")
                && list.get(2).equals("B")
                && list.get(3).equals("C")
                && list.get(4).equals("D")
                && list.get(5).equals("E")
                && list.get(6).equals("F")
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

    @Test
    public void reduce() {
        String r = CollectionList.of("A", "b", "C", "d").reduce(ICollectionList.Reducer.CONCAT_STRING);
        assert r.equals("AbCd") : r;
    }

    @Test
    public void reduceNumber() {
        int r = CollectionList.of(1, 2, 3, 4).reduce(ICollectionList.Reducer.SUM_INTEGER);
        assert r == 10 : r;
    }

    @Test
    public void reduceWithInitialValue() {
        String r = CollectionList.of("A", "b", "C", "d").reduce((o, s) -> o + s, "u");
        assert r.equals("uAbCd") : r;
    }

    @Test
    public void slice() {
        ICollectionList<String> list = CollectionList.of("ant", "bison", "camel", "duck", "elephant").slice(2);
        assert list.size() == 3 && list.contains("camel") && list.contains("duck") && list.contains("elephant") : getAssertionErrorMessage(list);
    }

    @Test
    public void sliceWithEnd() {
        ICollectionList<String> list = CollectionList.of("Banana", "Orange", "Lemon", "Apple", "Mango").slice(1, 3);
        assert list.size() == 2 && list.contains("Orange") && list.contains("Lemon") : getAssertionErrorMessage(list);
    }

    @Test
    public void limits() {
        assert CollectionList.of(1, 2, 3, 4, 5).limit(3).size() == 2;
        assert CollectionList.of(1, 2, 3, 4, 5).max(3).size() == 3;
    }
}
