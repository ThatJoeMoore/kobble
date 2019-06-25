package com.thatjoemoore.kobble.test

import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

fun KProperty0<*>.openDelegate(): Any? {
    val acc = isAccessible
    try {
        isAccessible = true
        return getDelegate()
    } finally {
        isAccessible = acc
    }
}