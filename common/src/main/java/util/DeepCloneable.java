package util;

import org.jetbrains.annotations.NotNull;
import util.reflect.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface DeepCloneable {
    /**
     * Deep clones the object.
     * This clone performs the full (deep) copy of the object not the shallow copy.
     * @return deep copy of this object
     */
    @NotNull
    Object deepClone();

    /**
     * Deep clones the object.
     * This clone performs the full(deep) copy
     * of the object, depending if object inherits
     * {@link DeepCloneable}, {@link Cloneable} or none.
     * If the object inherits DeepCloneable, it will
     * perform full copy of the object. If the object
     * inherits Cloneable, it will perform
     * (normal) shallow copy. If the object inherits
     * none of them, it just returns the passed
     * object without cloning.
     * @param o the object that will be cloned if supported.
     * @return the cloned object if success, returns param1 if not supported.
     */
    static Object clone(Object o) {
        try {
            if (o instanceof DeepCloneable) {
                return ((DeepCloneable) o).deepClone();
            } else if (o instanceof Cloneable) {
                Method cloneMethod = ReflectionHelper.findMethod(o.getClass(), "clone");
                if (cloneMethod != null) {
                    return ReflectionHelper.invokeMethod(o.getClass(), o, "clone");
                } else {
                    return o; // not cloneable, does not perform clone action.
                }
            } else {
                return o; // object does not inherits any cloneable interface
            }
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            return o;
        }
    }
}
