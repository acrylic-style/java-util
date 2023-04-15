package xyz.acrylicstyle.util.expression.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class ReflectionUtil {
    @Contract(pure = true)
    public static @NotNull String desc(@NotNull Method method) {
        StringBuilder args = new StringBuilder();
        for (Class<?> type : method.getParameterTypes()) {
            args.append(signature(type));
        }
        return "(" + args + ")" + signature(method.getReturnType());
    }

    public static @NotNull String signature(@NotNull Class<?> clazz) {
        return signature(clazz, "");
    }

    private static @NotNull String signature(@NotNull Class<?> clazz, String prefix) {
        if (clazz.isArray()) {
            return signature(clazz.getComponentType(), prefix + "[");
        }
        if (clazz == void.class) return prefix + "V";
        if (clazz == int.class) return prefix + "I";
        if (clazz == char.class) return prefix + "C";
        if (clazz == double.class) return prefix + "D";
        if (clazz == float.class) return prefix + "F";
        if (clazz == long.class) return prefix + "J";
        if (clazz == boolean.class) return prefix + "Z";
        if (clazz == byte.class) return prefix + "B";
        if (clazz == short.class) return prefix + "S";
        return prefix + "L" + clazz.getTypeName().replaceAll("\\.", "/") + ";";
    }
}
