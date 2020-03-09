# java-util
Java utils includes Collection(not java.util.Collection!), CollectionList and some amazing(probably) things(maybe)!

## Features

### public class CollectionList\<V\>

| Type Parameter | Method | Return Type | Parameter |
| ----- | ----- | ----- | ----- |
| \<T\> | [CollectionList#map](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/map) | `CollectionList<T>` | `Function<V, T>` function |
| | [CollectionList#filter](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/filter) | `CollectionList<V>` | `Function<V, Boolean>` filter |
| | CollectionList#reverse | `CollectionList<V>` | |
| | CollectionList#shuffle | `CollectionList<V>` | |

### public class JSONAPI
An easy api for call RESTFul APIs or any JSON APIs.

#### Constructor
- `public JSONAPI(@NotNull String url)`
  - Constructs JSONAPI instance with url.
- `public JSONAPI(@NotNull String url, @NotNull String method)`
  - Constructs JSONAPI instance with url and request method.
- `public JSONAPI(@NotNull String url, @NotNull String method, @NotNull RequestBody requestBody)`
  - Constructs JSONAPI instance with url, request method, and request body.
    - You can get `JSONAPI.RequestBody` via `JSONAPI.BodyBuilder`.

#### Methods
- `public JSONAPI.Response call()`

#### Inner Classes
- JSONAPI.Response
  - Contains JSONAPI response.
- JSONAPI.BodyBuilder
  - A Builder used for construct `JSONAPI.RequestBody`.
- (private) JSONAPI.RequestBody
  - A request body that contains properties map.

### public class RuntimeExceptionThrower

#### Static methods
- `public static <T> T invoke(ThrowableConsumer<T> t)`
  - Returns T but throws RuntimeException occurs when the consumer throws any exception.

#### Inner Interfaces
- ThrowableConsumer\<T\>
  - It's basically Consumer, but throwable.

### public class Watchdog
Thread with timeouts.

#### Methods
- `public Watchdog(String name, Runnable runnable, int timeout)` *\(Constructor\)*
- `public Watchdog then(Runnable runnable)`
  - Run `runnable` after watchdog task is done. May not be called if interrupted.
- `public synchronized void start()`
  - Starts task. It's almost same as `Thread#start()`.
  - It throws `UnsupportedOperationException` if this instance was constructed with `RunnableFunction`.
- `public synchronized Object startAwait() throws InterruptedException`
  - Starts task, but blocks main thread. Will be unblocked after the task is done or timeout was elapsed.
  - Returns `Object` only if `RunnableFunction` was provided.
- `public synchronized Object startAwaitWithoutException()`
  - Same as `Watchdog#startAwait()`, but it won't throw InterruptedException.
  - Returns `Object` only if `RunnableFunction` was provided.