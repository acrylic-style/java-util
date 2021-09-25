package util.list;

import java.util.ArrayList;

public class L1<T1> extends ArrayList<Object> {
    private final T1 t1;

    public L1(T1 t1) {
        this.add(this.t1 = t1);
    }

    public T1 getT1() { return this.t1; }
}
