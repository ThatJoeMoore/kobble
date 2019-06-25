package com.thatjoemoore.kobble

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface KobbleBinding<T> : ReadOnlyProperty<KobbleModule, T>

internal abstract class BaseBinding<T> : KobbleBinding<T> {
}

internal abstract class BaseSingletonBinding<T>: BaseBinding<T>() {
    protected abstract val value: T

    override fun getValue(thisRef: KobbleModule, property: KProperty<*>): T {
        return value
    }
}

internal class LazySingletonBinding<T>(supplier: Supplier<T>) : BaseSingletonBinding<T>() {
    override val value by lazy(supplier)
}

// TODO: Eventually, make the eagerness a little less eager - only call once we start retrieving bindings
internal class EagerSingletonBinding<T>(supplier: Supplier<T>) : BaseSingletonBinding<T>() {
    override val value = supplier()
}
