package util.magic;

import net.blueberrymc.native_util.NativeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;
import util.ActionableResult;
import util.ThrowableActionableResult;

import java.lang.reflect.Modifier;

public class Magic {
    public static final long VERSION = 1600L;

    private static int javaVersion = -1;

    // 7, 8, 9, 10, ...
    public static int getJavaVersion() {
        if (javaVersion != -1) return javaVersion;
        String version = System.getProperty("java.version");
        String[] arr = version.split("\\.");
        if (arr[0].equals("1")) {
            return javaVersion = Integer.parseInt(arr[1]);
        } else {
            return javaVersion = Integer.parseInt(arr[0]);
        }
    }

    /**
     * Returns forbidden magic which is not guaranteed to work across java versions, or even java platform. No
     * support will be provided for declared methods on forbidden magic. Their existence and behavior is not
     * guaranteed across future versions. They may be poorly named, throw exception, have misleading:
     * parameters, or any other bad programming practice. Programmers should not rely on these methods, as
     * they may be removed at anytime in the future. Use with caution!
     * <p><strong>Remember: This is forbidden magic, don't expect methods to work.</strong>
     * @return forbidden magic
     * @deprecated Going to be removed in 0.17 unless 0.17 adds new method
     */
    @Contract(value = " -> new", pure = true)
    @NotNull
    @Deprecated
    public static ForbiddenMagic getForbiddenMagic() { return new ForbiddenMagic(); }

    /**
     * This class provides methods that may be specific to a runtime. Their existence and behavior is not
     * guaranteed across future versions. They may be poorly named, throw exception, have misleading
     * parameters, or any other bad programming practice. Programmers should not rely on these methods, as
     * they may be removed at anytime in the future. Use with caution!
     * <p><strong>Remember: This is forbidden magic, don't expect methods to work.</strong>
     * @deprecated Going to be removed in 0.17 unless 0.17 adds new method
     */
    @Deprecated
    public static class ForbiddenMagic {
        /**
         * @deprecated because it's unsafe, and you can obtain the instance of unsafe in your code easily
         */
        @NotNull
        @Deprecated
        public ActionableResult<Unsafe> getUnsafe() {
            return ThrowableActionableResult.of(() -> (Unsafe) NativeUtil.get(Unsafe.class.getDeclaredField("theUnsafe"), null));
        }

        /**
         * Creates new instance.
         * @param clazz the class
         * @return new instance of the class
         * @throws RuntimeException if instance could not be created
         * @deprecated will be removed in 0.17. no longer magic, use {@link util.reflect.ReflectionHelper#createInstance(Class)} instead
         */
        @NotNull
        @Deprecated
        public <T> T createInstance(@NotNull Class<T> clazz) throws RuntimeException {
            if (clazz.isInterface()) throw new RuntimeException("Cannot create instance of the interface");
            if (Modifier.isAbstract(clazz.getModifiers())) throw new RuntimeException("Cannot create instance of the abstract class");
            if (clazz.isEnum()) throw new RuntimeException("Cannot create instance of the enum class");
            try {
                return clazz.newInstance();
            } catch (Exception ignore) {}
            return NativeUtil.allocateInstance(clazz);
        }

        /**
         * Return the Virtual Machine's Class object for the named primitive type. The result class is equivalent
         * to <i>&lt;BoxedPrimitiveType&gt;.TYPE</i>.
         * @throws ClassNotFoundException when class was not found by specified type.
         * @throws NullPointerException when null was provided to the type.
         * @return primitive class
         * @deprecated will be removed in 0.17
         */
        @NotNull
        @Deprecated
        public Class<?> getPrimitiveClass(@NotNull String type) throws ClassNotFoundException, NullPointerException, NoSuchMethodException {
            return (Class<?>) NativeUtil.invoke(
                    Class.class.getDeclaredMethod("getPrimitiveClass", String.class),
                    null,
                    type
            );
        }

        /**
         * Return the Virtual Machine's Class object for the named primitive type. The result class is equivalent
         * to <i>&lt;BoxedPrimitiveType&gt;.TYPE</i>. This method is similar to {@link #getPrimitiveClass(String)},
         * but it throws {@link RuntimeException} instead of {@link ClassNotFoundException} when could not find the class.
         * <p><strong>Remember: This is forbidden magic.</strong>
         * @throws RuntimeException when class was not found by specified type.
         * @throws NullPointerException when null was provided to the type.
         * @return primitive class
         * @deprecated will be removed in 0.17
         */
        @NotNull
        @Deprecated
        public Class<?> getPrimitiveClassSneaky(@NotNull String type) throws NullPointerException {
            try {
                return getPrimitiveClass(type);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
