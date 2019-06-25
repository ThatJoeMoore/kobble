package com.thatjoemoore.kobble

import com.thatjoemoore.kobble.test.openDelegate
import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class KobbleModuleTest {

    @Test
    fun `singleton() creates a lazy singleton binding`() {
        val obj = Any()
        val fixture = object: KobbleModule() {
            val foo by singleton { obj }
        }

        val delegate = fixture::foo.openDelegate()
        assertTrue(delegate is LazySingletonBinding<*>, "expected LazySingletonBinding, got $delegate")
    }

    @Test
    fun `singleton(lazy = false) creates an eager singleton binding`() {
        val obj = Any()
        val fixture = object: KobbleModule() {
            val foo by singleton(lazy = false) { obj }
        }

        val delegate = fixture::foo.openDelegate()
        assertTrue(delegate is EagerSingletonBinding<*>, "expected EagerSingletonBinding, got $delegate")
    }

    @Test
    fun `module keeps track of registered bindings`() {
        val fixture = object: KobbleModule() {
            val foo by singleton(lazy = false) { Any() }
            val bar: String by singleton { "baz" }
        }

        val result = fixture.bindings

        assertEquals(2, result.size)

        assertHasBinding<Any, EagerSingletonBinding<Any>>(
            result,
            fixture::foo
        )
        assertHasBinding<String, LazySingletonBinding<String>>(
            result,
            fixture::bar
        )
    }

    private inline fun <reified T, reified B: KobbleBinding<T>> assertHasBinding(
        bindings: Bindings,
        prop: KProperty<T>
    ) {
        val binding = bindings[prop.name]
        assertNotNull(binding, "expected property ${prop.name} to have been bound")
        assertTrue(binding is B, "Expected binding to be of type ${B::class.simpleName}, but was ${binding::class.simpleName}")
    }

}