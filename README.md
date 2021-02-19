# java-util
java-util includes Collection(not java.util.Collection!), CollectionList and some amazing things!

## Features

### Classes

- CollectionList - replaces List, with many useful methods.
- Collection - replaces HashMap
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
- reflector.* - Call internal class/methods without writing complex reflection code. (See `java-util-all/src/main/java/reflector/ReflectorTest.java`, `java-util-agent/src/main/java/util/agent/reflector/PrivateClass.java`)

## Note about Reflector
If it throws error like `java.lang.IllegalArgumentException: methods with same signature method() but incompatible return types: <some primitive type> and others`, you will need to do one of them to work properly.

### Method 1 (Easiest, and mostly it works)
Use [java agent](https://um.acrylicstyle.xyz/local/2021/02/10-25-01/java-util-agent-0.14.2a.jar) to workaround this.

This java agent is also useful for debugging your application, you can use jetbrains @NotNull to method parameter for non-null assertion at runtime.

Then run: `java -javaagent:/path/to/agent.jar ...` or with args: `java -javaagent:/path/to/agent.jar=verbose,debug,NotNullAssertionTransformer ...`

or create a file named `java` and add it to the PATH
```shell
#!/bin/bash
absolute/path/to/java -javaagent:/path/to/agent.jar $@
```

### Method 2 (Hard, but it should work all the time)
You have to insert these lines into the first line at sun/misc/ProxyGenerator.java#checkReturnTypes(List) to avoid errors.
```
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
