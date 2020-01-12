# java-util
Java utils includes Collection(not java.util.Collection!), CollectionList and some amazing(probably) things(maybe)!

Current latest version: [0.5.6](https://ci.acrylicstyle.xyz/job/java-util/lastSuccessfulBuild/artifact/target/Util-0.5.6.jar)

## Features

### CollectionList

| Type Parameter | Method | Return Type | Parameter |
| ----- | ----- | ----- | ----- |
| \<T\> | [CollectionList#map](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/map) | `CollectionList<T>` | `Function<V, T>` function |
| | [CollectionList#filter](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/filter) | `CollectionList<V>` | `Function<V, Boolean>` filter |
| | CollectionList#reverse | `CollectionList<V>` | |
| | CollectionList#shuffle | `CollectionList<V>` | |

