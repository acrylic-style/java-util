package util.kt.promise.rewrite

import org.junit.Test
import util.promise.rewrite.Promise

class PromiseKtTest {
    @Test
    fun destructTest() {
        val value = Promise.create<Int> { (resolve) ->
            resolve(40)
        }.then { it + 2 }.complete()
        assert(value == 42) { "Value was $value (expected: 42)" }
    }
}
