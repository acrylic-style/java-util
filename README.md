# java-util
java-util includes Collection(not java.util.Collection!), CollectionList and some amazing things!

## Features

### Classes

- CollectionList<V> - replaces List, with many useful methods.
- Collection<K, V> - replaces HashMap
- CollectionSync<K, V> - replaces Collection, "synchronized" at most methods
- CollectionStrictSync<K, V> - replaces CollectionSync, most methods are thread-safe. (Expects no ConcurrentModificationException)
- EventEmitter - Java implementation of Node.js EventEmitter
- promise.Promise - Partial implementation of JavaScript Promise class.
- javascript.JavaScript - Represents some keywords that can be found in javascript. Currently in development and not recommended.
- JSONAPI - Used to call any RESTful APIs
- ReflectionHelper - Helps you usage of reflection.
- Watchdog - Thread with a timeout, to prevent crashing application due to an execution timeout.
- StringCollection - HashMap dedicated for string key.
- CollectionSet - HashSet implementation, with ICollectionList methods (Some methods aren't supported)
- MathUtils - Provides some Math methods + methods
- reflect.* - helps you use of reflection (unlike ReflectionHelper, it has more methods, though, some methods are missing)
- reflector.* - Call internal class/methods without writing complex reflection code.

## Note about Reflector
If it throws error like `java.lang.IllegalArgumentException: methods with same signature method() but incompatible return types: <some primitive type> and others`, you will need the custom jdk to run this properly.

You have to insert these lines into the first line at sun/misc/ProxyGenerator.java#checkReturnTypes(List) to avoid errors.
```java
if (true) {
    if (methods.size() == 0) return;
    ProxyMethod m = methods.get(0);
    methods.clear();
    methods.add(m);
    return;
}
```

### Another methods to resolve that issue
- Replace (edit) the problematic class
- Try to avoid using problematic class
