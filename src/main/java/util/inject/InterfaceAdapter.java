package util.inject;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.openjpa.enhance.InstrumentationFactory;
import org.apache.openjpa.lib.log.NoneLogFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

class InterfaceAdapter implements ClassFileTransformer {
    private static boolean init = false;

    static void init() {
        if (init) return;
        Instrumentation instrumentation = InstrumentationFactory.getInstrumentation(new NoneLogFactory().getLog("InterfaceAdapter"));
        instrumentation.addTransformer(new InterfaceAdapter());
        init = true;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className.startsWith("java.")) return classfileBuffer;
        if (className.startsWith("javassist.")) return classfileBuffer;
        if (filter(Injector.data, data -> className.matches(data.getBaseClass())).size() != 0) {
            return transformClass(className, classfileBuffer);
        }
        return classfileBuffer;
    }

    public static <E> List<E> filter(List<E> list, Function<E, Boolean> function) {
        List<E> newList = new ArrayList<>();
        list.forEach(v -> {
            if (function.apply(v)) newList.add(v);
        });
        return newList;
    }

    public byte[] transformClass(String className, byte[] bytecode) {
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = pool.makeClass(new ByteArrayInputStream(bytecode));
            InjectorData data = Objects.requireNonNull(filter(Injector.data, dataaa -> className.matches(dataaa.getBaseClass())).get(0));
            if (data.getInterfaceClass().isInterface()) {
                CtClass interfaceClass = getCtClass(data.getInterfaceClass().getTypeName());
                clazz.addInterface(interfaceClass);
            } else {
                CtClass sup = getCtClass(data.getInterfaceClass().getTypeName());
                clazz.setSuperclass(sup);
            }
            return clazz.toBytecode();
        } catch (IOException | CannotCompileException ex) {
            throw new RuntimeException("Failed to instrument class: " + className, ex);
        }
    }

    public static CtClass getCtClass(String clazz) {
        try {
            Constructor<?> cconstructor = Class.forName("javassist.CtClassType").getDeclaredConstructor(String.class, ClassPool.class);
            cconstructor.setAccessible(true);
            return (CtClass) cconstructor.newInstance(clazz, ClassPool.getDefault());
        } catch (NoSuchMethodException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Could not create CtClass", e);
        }
    }
}
