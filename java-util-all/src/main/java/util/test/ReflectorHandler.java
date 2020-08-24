package util.test;

import org.jetbrains.annotations.NotNull;
import util.Validate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class ReflectorHandler implements InvocationHandler {
    private final Class<?> target;
    private final Object instance;

    @NotNull
    public Class<?> getTarget() {
        return target;
    }

    public ReflectorHandler(@NotNull Class<?> target, @NotNull Object instance) {
        Validate.notNull(target, "target cannot be null");
        Validate.notNull(instance, "instance cannot be null");
        this.target = target;
        this.instance = instance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("getClazz") && args == null) {
            return Object.class.getMethod("getClass").invoke(instance);
        }
        return target.getMethod(method.getName(), method.getParameterTypes()).invoke(instance, args);
    }

    public interface ClazzGetter {
        Class<?> getClazz();
    }
}
