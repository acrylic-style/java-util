# java-util
java-util includes Collection(not java.util.Collection!), CollectionList and some amazing things!

## Features

### Classes

- CollectionList<V> - replaces List
- Collection<K, V> - replaces HashMap
- CollectionSync<K, V> - replaces Collection, "synchronized" at most methods
- CollectionStrictSync<K, V> - replaces CollectionSync, most methods are thread-safe. (No ConcurrentModificationException)
- EventEmitter - Java implementation of Node.js EventEmitter
- promise.Promise - Partial implementation of JavaScript Promise class.
- javascript.JavaScript - Represents some keywords that can be found in javascript. Currently in development and not recommended.
- JSONAPI - Used to call any RESTful APIs
- ReflectionHelper - Helps you using reflection.
- Watchdog - Thread with timeout, to prevent application crash due to a timeout.
- StringCollection - HashMap dedicated for string key.
- CollectionSet - HashSet implementation, with ICollectionList methods (Some methods are not supported)
- MathUtils - Provides some Math methods + methods
- reflect.Ref - helps you using reflection (unlike ReflectionHelper, it has more methods)