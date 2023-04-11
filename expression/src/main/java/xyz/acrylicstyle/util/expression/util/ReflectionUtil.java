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
        if (clazz == void.class) return "V";
        if (clazz == int.class) return "I";
        if (clazz == char.class) return "C";
        if (clazz == double.class) return "D";
        if (clazz == float.class) return "F";
        if (clazz == long.class) return "J";
        if (clazz == boolean.class) return "Z";
        if (clazz == byte.class) return "B";
        if (clazz == short.class) return "S";
        return "L" + clazz.getTypeName().replaceAll("\\.", "/") + ";";
    }
}
