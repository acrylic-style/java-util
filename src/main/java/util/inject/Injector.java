package util.inject;

import java.util.ArrayList;
import java.util.List;

public class Injector {
    static {
        new InjectorData(null, null);
        InterfaceAdapter.init();
    }

    public static List<InjectorData> data = new ArrayList<>();

    public static void inject(Class<?> interfaceClass, String baseClass) {
        data.add(new InjectorData(interfaceClass, baseClass));
    }
}
