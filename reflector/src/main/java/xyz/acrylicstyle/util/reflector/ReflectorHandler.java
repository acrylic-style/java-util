package xyz.acrylicstyle.util.reflector;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.Memoize;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

// TODO: improve overall performance
public class ReflectorHandler implements InvocationHandler {
    private static final ReflectorOption EMPTY_OPTION = new ReflectorOption() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return ReflectorOption.class;
        }

        @Override
        public @NotNull String staticPrefix() {
            return ";";
        }

        @Override
        public ErrorOption errorOption() {
            return ErrorOption.DEFAULT;
        }

        @Override
        public YesNo suppressMessage() {
            return YesNo.DEFAULT;
        }
    };
    private final Class<?> target;
    private final Object instance;
    private ReflectorOption option;

    @NotNull
    public Class<?> getTarget() {
        return target;
    }

    public Object getInstance() { return instance; }

    /**
     * Creates new ReflectorHandler instance.
     * @param target the target class to call.
     * @param instance the instance. If null, it will be static ReflectorHandler and will be unable to call instance methods.
     */
    public ReflectorHandler(@NotNull Class<?> target, @Nullable Object instance) {
        Objects.requireNonNull(target, "target");
        this.target = target;
        this.instance = instance;
    }

    private static final Memoize<Optional<Object>> valueCache = new Memoize<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args_) throws Throwable {
        if (option == null) {
            option = method.getDeclaringClass().getAnnotation(ReflectorOption.class);
            if (option == null) option = EMPTY_OPTION;
        }

        // convert arguments
        Object[] args = parseFieldGetterParameter(method, args_);
        // find value from cache if it is cached
        Optional<Object> cache = valueCache.get(hashCode(), method.toGenericString(), args);
        //noinspection OptionalAssignedToNull
        if (cache != null) {
            return cache.orElse(null);
        }
        boolean isPure = pureAnnotationCache.computeIfAbsent(() -> method.isAnnotationPresent(Pure.class), method);
        if (method.isDefault()) {
            Object value = Reflector.methodExecutor.invokeSpecial(method, proxy, args_);
            if (isPure) valueCache.put(Optional.ofNullable(value), hashCode(), method.toGenericString(), args_);
            return value;
        }
        if (method.equals(ClazzGetter.METHOD) && (args == null || args.length == 0)) {
            if (instance == null) {
                throw new IllegalArgumentException("Cannot invoke Object#getClass with static ReflectorHandler");
            }
            return Object.class.getMethod("getClass").invoke(instance);
        }
        if (instance == null && method.getName().equals("hashCode") && (args == null || args.length == 0)) {
            // Attempt to call hashCode in static context
            return this.hashCode();
        }
        FieldGetter getter = fieldGetterAnnotationCache.computeIfAbsent(() -> method.getAnnotation(FieldGetter.class), method);
        FieldSetter setter = fieldSetterAnnotationCache.computeIfAbsent(() -> method.getAnnotation(FieldSetter.class), method);
        ForwardMethod forwardMethod = forwardMethodAnnotationCache.computeIfAbsent(() -> method.getAnnotation(ForwardMethod.class), method);
        CastTo castTo = castToAnnotationCache.computeIfAbsent(() -> method.getAnnotation(CastTo.class), method);
        ConstructorCall constructorCall = constructorCallAnnotationCache.computeIfAbsent(() -> method.getAnnotation(ConstructorCall.class), method);
        boolean isStatic = method.getName().startsWith(getOption(method).staticPrefix()) || method.isAnnotationPresent(Static.class);
        if (getter != null) {
            if (args != null && args.length > 0) throw new IllegalArgumentException("Requires exactly zero argument on method when applying @FieldGetter");
            Object value = getField(isStatic ? null : instance, proxy, getter, castTo, target, method);
            if (isPure) valueCache.put(Optional.ofNullable(value), hashCode(), method.toGenericString(), args_);
            return value;
        }
        if (setter != null) {
            if ((args == null || args.length == 0) || (args.length > 1)) throw new IllegalArgumentException("Requires exactly one argument on method when applying @FieldSetter");
            setField(isStatic ? null : instance, setter, target, method, args[0]);
            return null;
        }
        String methodName = forwardMethod == null ? method.getName() : forwardMethod.value();
        Executable found = null;
        if (constructorCall == null) {
            found = findMethod(target, methodName, convertArgsList(method, method.getParameterTypes()));
        }
        if (constructorCall != null || (methodName.equals("constructor") && found == null)) {
            Class<?> clazz = target;
            if (constructorCall != null && !constructorCall.value().equals(Object.class)) clazz = Reflector.reverseList.getOrDefault(constructorCall.value(), constructorCall.value());
            found = findConstructor(clazz, convertArgsList(method, method.getParameterTypes()))
                    .orElseThrow(() -> new NoSuchMethodException("Cannot find constructor with parameters " + Arrays.toString(method.getParameterTypes())));
            if (constructorCall != null && found == null) {
                if (getOption(method).errorOption() == ReflectorOption.ErrorOption.RETURN_NULL) {
                    return null;
                } else {
                    throw new NoSuchMethodException(method.toGenericString() + " is annotated @ConstructorCall, but could not find constructor with classes: " + Arrays.toString(convertArgsList(method, method.getParameterTypes())));
                }
            }
        }
        if (found != null) {
            if (castTo != null) {
                if (castTo.createInstance()) {
                    Object value;
                    try {
                        value = Reflector.methodExecutor.execute(found, isStatic ? null : Reflector.reverseInstanceList.getOrDefault(instance, instance), args);
                    } catch (IllegalArgumentException | ClassCastException ex) {
                        value = Reflector.methodExecutor.execute(found, instance, args);
                    }
                    Object inst = Reflector.methodExecutor.newInstance(castTo.value().getConstructor(Object.class), value);
                    if (isPure) valueCache.put(Optional.of(inst), hashCode(), method.toGenericString(), args_);
                    return inst;
                } else {
                    Object value;
                    try {
                        value = Reflector.methodExecutor.execute(found, isStatic ? null : Reflector.reverseInstanceList.getOrDefault(instance, instance), args);
                    } catch (IllegalArgumentException | ClassCastException ex) {
                        value = Reflector.methodExecutor.execute(found, instance, args);
                    }
                    Object inst = Reflector.castTo(null, proxy, value, castTo.value());
                    if (isPure) valueCache.put(Optional.ofNullable(inst), hashCode(), method.toGenericString(), args_);
                    return inst;
                    //return Reflector.castTo(null, method.getDeclaringClass(), proxy, methodName, method, castTo.value(), args);
                }
            }
            try {
                Object value = Reflector.methodExecutor.execute(found, isStatic ? null : instance, args);
                if (isPure) valueCache.put(Optional.ofNullable(value), hashCode(), method.toGenericString(), args_);
                return value;
            } catch (IllegalArgumentException ex) {
                ex.addSuppressed(new IllegalArgumentException(ex.getMessage() + ", missing @TransformParam, @FieldGetter, or @ForwardMethod?", ex));
                try {
                    Object value = Reflector.methodExecutor.execute(found, isStatic ? null : Reflector.reverseInstanceList.getOrDefault(instance, instance), args);
                    if (isPure) valueCache.put(Optional.ofNullable(value), hashCode(), method.toGenericString(), args_);
                    return value;
                } catch (IllegalArgumentException ex1) {
                    if (getOption(method).errorOption() == ReflectorOption.ErrorOption.RETURN_NULL) {
                        return null;
                    } else {
                        ex1.addSuppressed(ex);
                        throw ex1;
                    }
                }
            }
        } else {
            if (method.getName().startsWith("get") && method.getName().length() >= 4 && (args == null || args.length == 0)) {
                Object value = getField(isStatic ? null : instance, proxy, null, castTo, target, method);
                if (isPure) valueCache.put(Optional.ofNullable(value), hashCode(), method.toGenericString(), args_);
                return value;
            } else if (method.getName().startsWith("set") && method.getName().length() >= 4 && args != null && args.length == 1) {
                setField(isStatic ? null : instance, null, target, method, args[0]);
                return null;
            }
            if (getOption(method).errorOption() == ReflectorOption.ErrorOption.RETURN_NULL) {
                return null;
            } else {
                throw new NoSuchMethodException(method.toGenericString() + " on " + target.getCanonicalName());
            }
        }
    }

    private ReflectorOption getOption(@NotNull Method method) {
        ReflectorOption o = method.getAnnotation(ReflectorOption.class);
        if (o == null) return option;
        String staticPrefix = o.staticPrefix().equals(";") ? option.staticPrefix() : o.staticPrefix();
        ReflectorOption.ErrorOption errorOption = o.errorOption() == ReflectorOption.ErrorOption.DEFAULT ? option.errorOption() : o.errorOption();
        ReflectorOption.YesNo suppressMessage = o.suppressMessage() == ReflectorOption.YesNo.DEFAULT ? option.suppressMessage() : o.suppressMessage();
        return new ReflectorOption() {
            @Override
            public @NotNull String staticPrefix() {
                return staticPrefix;
            }

            @Override
            public ErrorOption errorOption() {
                return errorOption;
            }

            @Override
            public YesNo suppressMessage() {
                return suppressMessage;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return ReflectorOption.class;
            }
        };
    }

    private static Class<?>[] convertArgsList(Method method, Class<?>[] classes) {
        for (int i = 0; i < classes.length; i++) {
            int finalI = i;
            Annotation[] annotations = paramAnnotationCache.computeIfAbsent(() -> method.getParameterAnnotations()[finalI], method, finalI);
            if (annotations != null && annotations.length != 0) {
                List<Annotation> list = Arrays.asList(annotations);
                if (list.stream().anyMatch(annotation -> annotation.annotationType().equals(TransformParam.class))) {
                    if (Reflector.reverseList.containsKey(classes[i])) classes[i] = Reflector.reverseList.get(classes[i]);
                }
                list.stream().filter(annotation -> annotation.annotationType().equals(Type.class)).findFirst()
                        .map(annotation -> (Type) annotation)
                        .ifPresent(type -> {
                            try {
                                classes[finalI] = Class.forName(type.value());
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        }
        return classes;
    }

    private Object getField(@Nullable Object instance,
                            @NotNull Object proxy,
                            @Nullable FieldGetter getter,
                            @Nullable CastTo castTo,
                            @NotNull Class<?> target,
                            @NotNull Method method) throws Throwable {
        Field field = getField(getter == null ? null : getter.value(), target, method);
        if (field == null) return null;
        if (castTo != null) {
            if (castTo.createInstance()) {
                return castTo.value()
                        .getConstructor(Object.class)
                        .newInstance(Reflector.methodExecutor.getFieldValue(field, instance));
            } else {
                return Reflector.castFieldTo(method.getDeclaringClass(), proxy, field.getName(), castTo.value());
            }
        }
        return Reflector.methodExecutor.getFieldValue(field, instance);
    }

    private void setField(@Nullable Object instance, @Nullable FieldSetter setter, @NotNull Class<?> target, @NotNull Method method, @Nullable Object arg) throws NoSuchFieldException {
        Field field = getField(setter == null ? null : setter.value(), target, method);
        if (field == null) return;
        try {
            Reflector.methodExecutor.setFieldValue(field, instance, arg);
        } catch (ReflectiveOperationException e) {
            try {
                Reflector.methodExecutor.setFieldValue(field, Reflector.reverseInstanceList.get(instance), arg);
            } catch (ReflectiveOperationException e2) {
                e2.addSuppressed(e);
                throw new RuntimeException(e2);
            }
        }
    }

    @Nullable
    private Field getField(String value, Class<?> target, Method method) throws NoSuchFieldException {
        Field field;
        if (value == null || value.equals("")) {
            field = findField(target, method);
        } else {
            field = findField(target, value);
        }
        if (field == null) {
            if (getOption(method).errorOption() == ReflectorOption.ErrorOption.RETURN_NULL) {
                return null;
            } else {
                throw new NoSuchFieldException((value == null || value.equals("") ? fieldName(method) : value) + " on " + target.getCanonicalName());
            }
        }
        return field;
    }

    private static final Memoize<Annotation[]> paramAnnotationCache = new Memoize<>();
    private static final Memoize<FieldGetter> fieldGetterAnnotationCache = new Memoize<>();
    private static final Memoize<FieldSetter> fieldSetterAnnotationCache = new Memoize<>();
    private static final Memoize<ForwardMethod> forwardMethodAnnotationCache = new Memoize<>();
    private static final Memoize<CastTo> castToAnnotationCache = new Memoize<>();
    private static final Memoize<ConstructorCall> constructorCallAnnotationCache = new Memoize<>();
    private static final Memoize<Boolean> pureAnnotationCache = new Memoize<>();

    @Contract("_, _ -> param2")
    private static Object @Nullable [] parseFieldGetterParameter(@NotNull Method method, @Nullable Object[] args) throws Throwable {
        if (args == null) return null;
        for (int i = 0; i < method.getParameters().length; i++) {
            int idx = i;
            Object arg = args[i];
            Annotation[] annotations = paramAnnotationCache.computeIfAbsent(() -> method.getParameterAnnotations()[idx], method, idx);
            if (annotations != null && annotations.length != 0) {
                if (Arrays.stream(annotations).anyMatch(annotation -> annotation.annotationType().equals(TransformParam.class))) {
                    Optional<Object> unproxiedInstance = Reflector.unwrap(arg);
                    if (unproxiedInstance.isPresent()) {
                        args[i] = arg = unproxiedInstance.get();
                    }
                }
            }
            if (arg == null) continue;
            FieldGetter getter = fieldGetterAnnotationCache.computeIfAbsent(() -> method.getParameters()[idx].getAnnotation(FieldGetter.class), method);
            ForwardMethod forwardMethod = forwardMethodAnnotationCache.computeIfAbsent(() -> method.getParameters()[idx].getAnnotation(ForwardMethod.class), method);
            if (getter == null && forwardMethod == null) continue;
            Class<?> target = arg.getClass();
            if (getter != null) {
                if (!getter.target().equals(Object.class)) target = getter.target();
                Field field = findField(target, getter.value());
                if (field == null) throw new NoSuchFieldException("Could not find field " + target.getCanonicalName() + "#" + getter.value());
                args[i] = Reflector.methodExecutor.getFieldValue(field, arg);
            }
            if (forwardMethod != null) {
                if (!forwardMethod.target().equals(Object.class)) target = forwardMethod.target();
                Method m = findMethod(target, forwardMethod.value());
                if (m == null) throw new NoSuchMethodException("Could not find method " + target.getCanonicalName() + "#" + forwardMethod.value());
                args[i] = Reflector.methodExecutor.invoke(m, arg);
            }
        }
        return args;
    }

    @NotNull
    private static String fieldName(Method method) {
        return deCapitalize(method.getName().replaceFirst("[gs]et", ""));
    }

    @Nullable
    private static Field findField(Class<?> target, Method method) {
        return findField(target, fieldName(method));
    }

    private static final Memoize<Optional<Method>> methodCache = new Memoize<>();

    private static <T> @Nullable Method findMethod(@NotNull Class<? extends T> clazz, @NotNull String methodName, @NotNull Class<?>... args) {
        Optional<Method> cache = methodCache.get(clazz, methodName, args);
        // This is intentional because Memoize#get could return null
        //noinspection OptionalAssignedToNull
        if (cache != null) {
            return cache.orElse(null);
        }
        Method method = null;
        Method implMethod = null;
        for (Class<?> cl : getSupers(clazz, true)) {
            try {
                Method m = findAssignableMethod(cl, methodName, args);
                if (m.isDefault() || (m.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT) {
                    implMethod = m;
                }
                method = m;
            } catch (NoSuchMethodException ignore) {}
        }
        if (implMethod != null) {
            // prefer implMethod
            method = implMethod;
        }
        methodCache.put(Optional.ofNullable(method), clazz, methodName, args);
        return method;
    }

    private static final Memoize<Object /* = Method | Throwable */> assignableMethodCache = new Memoize<>();

    @Contract(pure = true)
    private static @NotNull Method findAssignableMethod(@NotNull Class<?> clazz, @NotNull String methodName, @NotNull Class<?> @NotNull ... args) throws NoSuchMethodException {
        Object cache = assignableMethodCache.get(clazz, methodName, args);
        if (cache instanceof Method) {
            return (Method) cache;
        } else if (cache instanceof Throwable) {
            throw (NoSuchMethodException) cache;
        }
        NoSuchMethodException ex;
        try {
            // try exact match
            Method method = clazz.getDeclaredMethod(methodName, args);
            assignableMethodCache.put(method, clazz, methodName, args);
            return method;
        } catch (NoSuchMethodException e) {
            ex = e; // throw later if no match found
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == args.length) {
                boolean match = true;
                for (int i = 0; i < args.length; i++) {
                    if (!method.getParameterTypes()[i].isAssignableFrom(args[i])) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    assignableMethodCache.put(method, clazz, methodName, args);
                    return method;
                }
            }
        }
        assignableMethodCache.put(ex, clazz, methodName, args);
        throw ex;
    }

    @NotNull
    public <T> Optional<Constructor<? super T>> findConstructor(@NotNull Class<T> clazz, @Nullable Class<?>... types) {
        try {
            Constructor<? super T> constructor = clazz.getDeclaredConstructor(types);
            return Optional.of(constructor);
        } catch (ReflectiveOperationException e) {
            return Optional.empty();
        }
    }

    private static final Memoize<Optional<Field>> fieldCache = new Memoize<>();

    @Contract(pure = true)
    @Nullable
    private static <T> Field findField(@NotNull Class<? extends T> clazz, @NotNull String fieldName) {
        Optional<Field> cache = fieldCache.get(clazz, fieldName);
        //noinspection OptionalAssignedToNull
        if (cache != null) {
            return cache.orElse(null);
        }
        for (Class<?> cl : getSupers(clazz, true)) {
            try {
                Field f = cl.getDeclaredField(fieldName);
                fieldCache.put(Optional.of(f), clazz, fieldName);
                return f;
            } catch (NoSuchFieldException ignore) {}
        }
        fieldCache.put(Optional.empty(), clazz, fieldName);
        return null;
    }

    @NotNull
    private static String deCapitalize(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    private static Method getClazzGetterMethod() {
        try {
            return ClazzGetter.class.getMethod("getClazz");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the list of all superclasses and interfaces in hierarchy of the given class.
     * @param clazz the class
     * @return the classes
     */
    private static @NotNull Collection<@NotNull Class<?>> getSupers(@NotNull Class<?> clazz, boolean includeSelf) {
        Set<Class<?>> list = new HashSet<>();
        if (includeSelf) {
            list.add(clazz);
        }
        Class<?> superclass = clazz;
        while ((superclass = superclass.getSuperclass()) != null) {
            list.add(superclass);
            list.addAll(getSupers(superclass, false));
        }
        for (Class<?> anInterface : clazz.getInterfaces()) {
            list.addAll(getSupers(anInterface, true));
        }
        return list;
    }

    public interface ClazzGetter {
        Method METHOD = getClazzGetterMethod();

        Class<?> getClazz();
    }
}
