package util.list;

import java.util.ArrayList;

public class L4<T1, T2, T3, T4> extends ArrayList<Object> {
    public final T1 t1;
    public final T2 t2;
    public final T3 t3;
    public final T4 t4;

    public L4(T1 t1, T2 t2, T3 t3, T4 t4) {
        this.add(this.t1 = t1);
        this.add(this.t2 = t2);
        this.add(this.t3 = t3);
        this.add(this.t4 = t4);
    }
}
