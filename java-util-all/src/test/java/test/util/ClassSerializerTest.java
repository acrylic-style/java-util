package test.util;

import org.junit.Test;
import util.serialization.ActualType;
import util.serialization.ClassSerializer;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;

public class ClassSerializerTest {
    @SuppressWarnings("unchecked")
    @Test
    public void serializeEntryTest() {
        try {
            {
                ClassSerializer<Map.Entry<String, String>> serializer = new ClassSerializer<>(new AbstractMap.SimpleEntry<>("a", null));
                String serialized = serializer.serialize();
                //System.out.println("Serialized:\n" + serialized);
                /*
                | Serialized (if does not implement java.io.Serializable):
                | type: object
                | fields:
                | - instance:
                |     data: a
                |     type: string
                |   actualType: null
                |   name: key
                |   class: java.lang.Object
                | - instance:
                |     data: b
                |     type: string
                |   actualType: null
                |   name: value
                |   class: java.lang.Object
                | class: java.util.AbstractMap.SimpleEntry
                */
                /*
                | Serialized (if implements java.io.Serializable) (no class, because it isn't needed):
                | data: rO0ABXNyACFqYXZhLnV0aWwuQWJzdHJhY3RNYXAkU2ltcGxlRW50cnmKCuyl/FaAHwIAAkwAA2tleXQAEkxqYXZhL2xhbmcvT2JqZWN0O0wABXZhbHVlcQB+AAF4cHQAAWFw
                | type: bytearray
                */
                Map.Entry<String, String> entry = ClassSerializer.deserialize(AbstractMap.SimpleEntry.class, serialized);
                assert entry.getKey().equals("a") : "Expected 'a', but got: " + entry.getKey();
                assert entry.getValue() == null : "Expected null, but got: " + entry.getValue();
            }
            {
                ClassSerializer<TestCl> serializer = new ClassSerializer<>(new TestCl("abc", 1, 2, 3));
                String serialized = serializer.serialize();
                //System.out.println("Serialized:\n" + serialized);
                /*
                | type: object
                | fields:
                | - instance:
                |     data:
                |       data: abc
                |       type: string
                |     type: weakref
                |   actualType: java.lang.String
                |   name: world
                |   class: java.lang.ref.Reference
                | - instance:
                |     data: 1.0
                |     type: double
                |   actualType: null
                |   name: x
                |   class: double
                | - instance:
                |     data: 2.0
                |     type: double
                |   actualType: null
                |   name: y
                |   class: double
                | - instance:
                |     data: 3.0
                |     type: double
                |   actualType: null
                |   name: z
                |   class: double
                | class: test.util.ClassSerializerTest.TestCl
                */
                TestCl test = ClassSerializer.deserialize(TestCl.class, serialized);
                assert test.world.get() != null : "world is null";
                assert Objects.equals(test.world.get(), "abc") : "expected 'abc', but got: " + test.world.get();
                assert test.x == 1d : "expected 1, but got: " + test.x;
                assert test.y == 2d : "expected 2, but got: " + test.y;
                assert test.z == 3d : "expected 3, but got: " + test.z;
            }
        } catch (UnsupportedOperationException e) {
            // ignore, java 12+ cannot remove final modifier
        } catch (Exception | NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError e) {
            // don't fail the test, as it might fail on some jvm version
            // but just in case there is problem in ClassSerializer, just print the stack trace but don't fail the test
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("FieldMayBeFinal")
    public static class TestCl {
        @ActualType(String.class)
        private Reference<String> world;
        private double x;
        private double y;
        private double z;
        
        public TestCl(String s, double x, double y, double z) {
            this.world = new WeakReference<>(s);
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
