package util;

import java.util.List;

public class NonNullCollectionList<C extends NonNullCollectionList<C, V>, V> extends CollectionList<C, V> {
    private static final long serialVersionUID = 11_27L;

    public NonNullCollectionList() { super(); }

    public NonNullCollectionList(List<? extends V> list) {
        super(list);
    }

    @SafeVarargs
    public NonNullCollectionList(V... v) {
        super(v);
    }

    public NonNullCollectionList(java.util.Collection<? extends V> list) {
        super(list);
    }

    @Override
    public void add(int index, V element) {
        if (element == null) return;
        super.add(index, element);
    }

    @Override
    public boolean add(V v) {
        if (v == null) return true;
        return super.add(v);
    }

    @Override
    public V get(int index) {
        if (this.isEmpty()) throw new IndexOutOfBoundsException("list is empty");
        V v = super.get(index);
        if (v == null) {
            this.remove(index);
            return this.get(index);
        }
        return v;
    }
}
