package util.kt.reflect

import java.lang.reflect.AccessibleObject

fun <T: AccessibleObject> T.accessible(flag: Boolean): T = this.apply { isAccessible = flag }
