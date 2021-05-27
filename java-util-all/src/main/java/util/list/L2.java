package util.list;

import java.util.ArrayList;

public class L2<T1, T2> extends ArrayList<Object> {
    public final T1 t1;
    public final T2 t2;

    public L2(T1 t1, T2 t2) {
        this.add(this.t1 = t1);
        this.add(this.t2 = t2);
    }
}
