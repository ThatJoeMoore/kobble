package com.thatjoemoore.kobble

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class KobbleModule {

    protected fun <T> singleton(
        lazy: Boolean = true,
        supplier: Supplier<T>
    ): BindingFactory<T> {
        return buildBinding {
            if (lazy) {
                LazySingletonBinding(supplier)
            } else {
                EagerSingletonBinding(supplier)
            }
        }
    }

    private val _bindings: MutableBindings = mutableMapOf()

    internal val bindings: Bindings = _bindings

    private inline fun <T> buildBinding(crossinline buildBinding: () -> KobbleBinding<T>): BindingFactory<T> {
        return object : BindingFactory<T> {
            override fun provideDelegate(thisRef: KobbleModule, prop: KProperty<*>): ReadOnlyProperty<KobbleModule, T> {
                val binding = buildBinding()
                thisRef._bindings[prop.name] = binding
                return binding
            }
        }
    }

}

interface BindingFactory<T> {
    operator fun provideDelegate(
        thisRef: KobbleModule,
        prop: KProperty<*>
    ): ReadOnlyProperty<KobbleModule, T>
}

internal typealias Bindings = Map<String, KobbleBinding<*>>
internal typealias MutableBindings = MutableMap<String, KobbleBinding<*>>
