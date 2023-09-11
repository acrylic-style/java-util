/*
 * Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package xyz.acrylicstyle.util.expression.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AbstractFinder<T extends Executable> {
    private final String name;
    private final Class<?>[] args;

    public AbstractFinder(@NotNull String name, @NotNull Class<?> @NotNull ... args) {
        this.name = name;
        this.args = args;
    }

    public boolean isValid(@NotNull T t) {
        return /*Modifier.isPublic(t.getModifiers()) && */t.getName().equals(this.name);
    }

    public boolean isAssignable(Class<?>[] min, Class<?>[] max) {
        for (int i = 0; i < this.args.length; i++) {
            if (null != this.args[i]) {
                if (!min[i].isAssignableFrom(max[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    public final @NotNull T find(T @NotNull [] methods) throws NoSuchMethodException {
        Map<T, Class<?>[]> map = new HashMap<>();

        T oldMethod = null;
        Class<?>[] oldParams = null;
        boolean ambiguous = false;

        for (T newMethod : methods) {
            if (isValid(newMethod)) {
                Class<?>[] newParams = newMethod.getParameterTypes();
                if (newParams.length == this.args.length) {
                    boolean assignable = isAssignable(newParams, this.args);
                    if (!assignable) {
                        PrimitiveWrapperMap.replacePrimitivesWithWrappers(newParams);
                        assignable = isAssignable(newParams, this.args);
                    }
                    if (assignable) {
                        if (oldMethod == null) {
                            oldMethod = newMethod;
                            oldParams = newParams;
                        } else {
                            boolean useNew = isAssignable(oldParams, newParams);
                            boolean useOld = isAssignable(newParams, oldParams);

                            if (useOld && useNew) {
                                // only if parameters are equal
                                useNew = !newMethod.isSynthetic();
                                useOld = !oldMethod.isSynthetic();
                            }
                            if (useOld == useNew) {
                                ambiguous = true;
                            } else if (useNew) {
                                oldMethod = newMethod;
                                oldParams = newParams;
                                ambiguous = false;
                            }
                        }
                    }
                }
                if (newMethod.isVarArgs()) {
                    int length = newParams.length - 1;
                    if (length <= this.args.length) {
                        Class<?>[] array = new Class<?>[this.args.length];
                        System.arraycopy(newParams, 0, array, 0, length);
                        if (length < this.args.length) {
                            Class<?> type = newParams[length].getComponentType();
                            if (type.isPrimitive()) {
                                type = PrimitiveWrapperMap.getType(type.getName());
                            }
                            for (int i = length; i < this.args.length; i++) {
                                array[i] = type;
                            }
                        }
                        map.put(newMethod, array);
                    }
                }
            }
        }
        for (T newMethod : methods) {
            Class<?>[] newParams = map.get(newMethod);
            if (newParams != null) {
                if (isAssignable(newParams, this.args)) {
                    if (oldMethod == null) {
                        oldMethod = newMethod;
                        oldParams = newParams;
                    } else {
                        boolean useNew = isAssignable(oldParams, newParams);
                        boolean useOld = isAssignable(newParams, oldParams);

                        if (useOld && useNew) {
                            // only if parameters are equal
                            useNew = !newMethod.isSynthetic();
                            useOld = !oldMethod.isSynthetic();
                        }
                        if (useOld == useNew) {
                            if (oldParams == map.get(oldMethod)) {
                                ambiguous = true;
                            }
                        } else if (useNew) {
                            oldMethod = newMethod;
                            oldParams = newParams;
                            ambiguous = false;
                        }
                    }
                }
            }
        }

        if (false && ambiguous) {
            throw new NoSuchMethodException("Ambiguous methods are found");
        }
        if (oldMethod == null) {
            throw new NoSuchMethodException("No such method " + name + Arrays.toString(args));
        }
        return oldMethod;
    }

    /**
     * Finds method that is accessible from public class or interface through class hierarchy.
     *
     * @param method  object that represents found method
     * @return object that represents accessible method
     * @throws NoSuchMethodException if method is not accessible or is not found
     *                               in specified superclass or interface
     */
    public static Method findAccessibleMethod(Method method) throws NoSuchMethodException {
        Class<?> type = method.getDeclaringClass();
        if (Modifier.isPublic(type.getModifiers())) {
            return method;
        }
        if (Modifier.isStatic(method.getModifiers())) {
            throw new NoSuchMethodException("Method '" + method.getName() + "' is not accessible");
        }
        for (Type generic : type.getGenericInterfaces()) {
            try {
                return findAccessibleMethod(method, generic);
            }
            catch (NoSuchMethodException exception) {
                // try to find in superclass or another interface
            }
        }
        return findAccessibleMethod(method, type.getGenericSuperclass());
    }

    /**
     * Finds method that accessible from specified class.
     *
     * @param method  object that represents found method
     * @param generic generic type that is used to find accessible method
     * @return object that represents accessible method
     * @throws NoSuchMethodException if method is not accessible or is not found
     *                               in specified superclass or interface
     */
    private static Method findAccessibleMethod(Method method, Type generic) throws NoSuchMethodException {
        String name = method.getName();
        Class<?>[] params = method.getParameterTypes();
        if (generic instanceof Class) {
            Class<?> type = (Class<?>) generic;
            return findAccessibleMethod(type.getMethod(name, params));
        }
        if (generic instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) generic;
            Class<?> type = (Class<?>) pt.getRawType();
            for (Method m : type.getMethods()) {
                if (m.getName().equals(name)) {
                    Class<?>[] pts = m.getParameterTypes();
                    if (pts.length == params.length) {
                        if (Arrays.equals(params, pts)) {
                            return findAccessibleMethod(m);
                        }
                        Type[] gpts = m.getGenericParameterTypes();
                        if (params.length == gpts.length) {
                            if (Arrays.equals(params, TypeResolver.erase(TypeResolver.resolve(pt, gpts)))) {
                                return findAccessibleMethod(m);
                            }
                        }
                    }
                }
            }
        }
        throw new NoSuchMethodException("Method '" + name + "' is not accessible");
    }
}
