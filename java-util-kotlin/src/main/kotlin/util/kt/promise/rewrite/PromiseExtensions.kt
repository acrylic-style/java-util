package util.kt.promise.rewrite

import util.promise.rewrite.Promise
import util.promise.rewrite.PromiseContext

fun <T> Promise<T>.catch(handler: (Throwable) -> Unit) = this.onCatch(handler)

// resolve
operator fun <T> PromiseContext<T>.component1(): (value: T?) -> Unit = { value: T? -> this.resolve(value) }

// reject
operator fun <T> PromiseContext<T>.component2(): (value: Throwable?) -> Unit = { error: Throwable? -> this.reject(error) }
