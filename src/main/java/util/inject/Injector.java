package util.inject;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Injector {
    static {
        new InjectorData(null, null);
        InterfaceAdapter.init();
    }

    public static List<InjectorData> data = new ArrayList<>();

    public static void inject(Class<?> interfaceClass, String baseClass) {
        data.add(new InjectorData(interfaceClass, baseClass));
    }

    @SneakyThrows({ClassNotFoundException.class, IllegalAccessException.class, NoSuchFieldException.class})
    public static <T> Object getField(@NotNull Class<T> clazz, T instance, @NotNull String field) {
        List<InjectorData> datas = InterfaceAdapter.filter(data, d -> d.getInterfaceClass().equals(clazz));
        if (datas.size() == 0) return null;
        Class<?> claz = Class.forName(datas.get(0).getBaseClass().replaceAll("/", "."));
        Field field1 = claz.getDeclaredField(field);
        field1.setAccessible(true);
        return field1.get(instance);
    }

    @SneakyThrows({ClassNotFoundException.class, IllegalAccessException.class, NoSuchFieldException.class})
    public static <T> void setField(@NotNull Class<T> clazz, T instance, @NotNull String field, Object value) {
        List<InjectorData> datas = InterfaceAdapter.filter(data, d -> d.getInterfaceClass().equals(clazz));
        if (datas.size() == 0) return;
        Class<?> claz = Class.forName(datas.get(0).getBaseClass().replaceAll("/", "."));
        Field field1 = claz.getDeclaredField(field);
        field1.setAccessible(true);
        field1.set(instance, value);
    }

    public static <T> void updateField(@NotNull Class<T> clazz, T instance, @NotNull String field) {
        try {
            List<InjectorData> datas = InterfaceAdapter.filter(data, d -> d.getInterfaceClass().equals(clazz));
            if (datas.size() == 0) return;
            Class<?> claz = Class.forName(datas.get(0).getBaseClass().replaceAll("/", "."));
            Field field1 = claz.getDeclaredField(field);
            field1.setAccessible(true);
            Object value = field1.get(instance);
            Field field2 = clazz.getDeclaredField(field);
            field2.setAccessible(true);
            if(value != field2.get(instance)) field2.set(instance, value);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException ignored) {}
    }

    private static final Timer timer = new Timer(true);

    /*
     * This method doesn't needs to be called manually, it will be called automatically.
     */
    public static <T> void updateAllFields(@NotNull Class<T> clazz, T instance) {
        for (Field field : clazz.getDeclaredFields()) {
            updateField(clazz, instance, field.getName());
        }
    }

    /*
     * This method doesn't needs to be called manually, it will be called automatically.
     */
    public static <T> void updateAllFieldsTimer(@NotNull Class<T> clazz, T instance) {
        updateAllFields(clazz, instance);
        timer.schedule(createNewTimerTask(clazz, instance), 500);
    }

    private static <T> TimerTask createNewTimerTask(Class<T> clazz, T instance) {
        return new TimerTask() {
            @Override
            public void run() {
                updateAllFields(clazz, instance);
            }
        };
    }
}
