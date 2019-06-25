package com.thatjoemoore.kobble

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

internal class EagerSingletonBindingTest {

    @Test
    fun `always returns same instance`() {
        val fixture = EagerSingletonBinding { Any() }

        val result = fixture.testInvoke()
        assertNotNull(result)
        assertSame(result, fixture.testInvoke(), "expected fixture.foo to return the same object")
    }

    @Test
    fun `eagerly calls the initializer`() {
        var called = false

        val fixture = EagerSingletonBinding {
            called = true
            Any()
        }

        assertTrue(called, "Expected initializer to have been called")
    }

    private fun <T> EagerSingletonBinding<T>.testInvoke(): T {
        return this.getValue(FakeModule, FakeModule::foo)
    }

    private object FakeModule : KobbleModule() {
        val foo: Any = Any()
    }

}