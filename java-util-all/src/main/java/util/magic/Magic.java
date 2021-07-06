package util.magic;

import net.blueberrymc.native_util.NativeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;
import util.ActionableResult;
import util.ThrowableActionableResult;
import util.reflect.Ref;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class Magic {
    public static final long VERSION = 1500L;

    private static int javaVersion = -1;

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
     */
    @Contract(value = " -> new", pure = true)
    @NotNull
    public static ForbiddenMagic getForbiddenMagic() { return new ForbiddenMagic(); }

    /**
     * This class provides methods that may be specific to a runtime. Their existence and behavior is not
     * guaranteed across future versions. They may be poorly named, throw exception, have misleading
     * parameters, or any other bad programming practice. Programmers should not rely on these methods, as
     * they may be removed at anytime in the future. Use with caution!
     * <p><strong>Remember: This is forbidden magic, don't expect methods to work.</strong>
     */
    public static class ForbiddenMagic {
        @NotNull
        public ActionableResult<Unsafe> getUnsafe() {
            return ThrowableActionableResult.of(() -> (Unsafe) Ref.getClass(Unsafe.class).getDeclaredField("theUnsafe").accessible(true).get(null));
        }

        /**
         * Creates new instance.
         * <p><strong>Remember: This is forbidden magic.</strong>
         * @param clazz the class that you want to create instance
         * @return new instance of the class
         * @throws RuntimeException if instance could not be created
         * @throws Error if method or class could not be found
         */
        @SuppressWarnings("unchecked")
        @NotNull
        public <T> T createInstance(@NotNull Class<T> clazz) throws RuntimeException, Error {
            if (clazz.isInterface()) throw new RuntimeException("Cannot create instance of the interface");
            if (Modifier.isAbstract(clazz.getModifiers())) throw new RuntimeException("Cannot create instance of the abstract class");
            if (clazz.isEnum()) throw new RuntimeException("Cannot create instance of the enum class");
            try {
                // first, try the normal reflection
                return clazz.newInstance();
            } catch (Throwable throwable) {
                // ignore
            }
            try {
                // then try native method
                return NativeUtil.allocateInstance(clazz);
            } catch (Throwable throwable) {
                // ignore
            }
            if (getUnsafe().isPresent()) {
                // and try unsafe if possible
                Unsafe unsafe = getUnsafe().getOrThrow();
                try {
                    return (T) unsafe.allocateInstance(clazz);
                } catch (InstantiationException e) {
                    // we throw exception here, because it's impossible to create instance anyway
                    throw new RuntimeException(e);
                }
            }
            try {
                // then try ReflectionFactory
                Constructor<Object> objectConstructor = Object.class.getDeclaredConstructor();
                Constructor<?> constructor = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(clazz, objectConstructor);
                return clazz.cast(constructor.newInstance());
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Return the Virtual Machine's Class object for the named primitive type. The result class is equivalent
         * to <i>&lt;BoxedPrimitiveType&gt;.TYPE</i>.
         * <p><strong>Remember: This is forbidden magic.</strong>
         * @throws ClassNotFoundException when class was not found by specified type.
         * @throws NullPointerException when null was provided to the type.
         * @return primitive class
         */
        @NotNull
        public Class<?> getPrimitiveClass(@NotNull String type) throws ClassNotFoundException, NullPointerException {
            return (Class<?>) Ref.getClass(Class.class)
                    .getDeclaredMethod("getPrimitiveClass", String.class)
                    .accessible(true)
                    .invoke(null, type);
        }

        /**
         * Return the Virtual Machine's Class object for the named primitive type. The result class is equivalent
         * to <i>&lt;BoxedPrimitiveType&gt;.TYPE</i>. This method is similar to {@link #getPrimitiveClass(String)},
         * but it throws {@link RuntimeException} instead of {@link ClassNotFoundException} when could not find the class.
         * <p><strong>Remember: This is forbidden magic.</strong>
         * @throws RuntimeException when class was not found by specified type.
         * @throws NullPointerException when null was provided to the type.
         * @return primitive class
         */
        @NotNull
        public Class<?> getPrimitiveClassSneaky(@NotNull String type) throws NullPointerException {
            try {
                return getPrimitiveClass(type);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
