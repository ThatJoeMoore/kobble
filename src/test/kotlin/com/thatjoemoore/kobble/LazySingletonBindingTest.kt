package com.thatjoemoore.kobble

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

internal class LazySingletonBindingTest {

    @Test
    fun `always returns same instance`() {
        val fixture = LazySingletonBinding { Any() }

        val result = fixture.testInvoke()
        assertNotNull(result)
        assertSame(result, fixture.testInvoke(), "expected fixture.foo to return the same object")
    }

    @Test
    fun `lazily calls the initializer`() {
        var called = false

        val fixture = LazySingletonBinding {
            called = true
            Any()
        }

        assertFalse(called, "Expected initializer to not have been called yet")

        fixture.testInvoke()

        assertTrue(called, "Expected initializer to have been called")
    }

    private fun <T> LazySingletonBinding<T>.testInvoke(): T {
        return this.getValue(FakeModule, FakeModule::foo)
    }

    private object FakeModule : KobbleModule() {
        val foo: Any = Any()
    }

}