package util.agent.reflector;

import org.jetbrains.annotations.NotNull;
import util.reflector.CastTo;
import util.reflector.FieldGetter;
import util.reflector.FieldSetter;
import util.reflector.ForwardMethod;
import util.reflector.Reflector;
import util.reflector.ReflectorHandler;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface PrivateClass {
    @NotNull
    static PrivateClass make(@NotNull Class<?> clazz) {
        return Reflector.newReflector(null, PrivateClass.class, new ReflectorHandler(Class.class, clazz));
    }

    @CastTo(AnnotationData.class)
    @ForwardMethod("annotationData")
    AnnotationData getAnnotationData();

    interface AnnotationData {
        @FieldGetter("annotations")
        Map<Class<? extends Annotation>, Annotation> getAnnotations();

        @FieldSetter("annotations")
        void setAnnotations(Map<Class<? extends Annotation>, Annotation> map);

        @FieldGetter("declaredAnnotations")
        Map<Class<? extends Annotation>, Annotation> getDeclaredAnnotations();

        @FieldSetter("declaredAnnotations")
        void setDeclaredAnnotations(Map<Class<? extends Annotation>, Annotation> map);
    }
}
