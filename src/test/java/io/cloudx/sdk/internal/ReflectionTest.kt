package io.cloudx.sdk.internal

import org.junit.Test

class ReflectionTest {

    @Test
    fun objectInstance_Should_Return_NonNullInstance_Of_DummyInterface() {
        objectInstance<DummyInterface>(DummyObject::class.qualifiedName!!)
            .assertInstanceOfDummyInterfaceShouldBeNotNull()
    }

    private fun Any?.assertInstanceOfDummyInterfaceShouldBeNotNull() {
        assert(this != null) {
            "Expected instance of ${DummyInterface::class}, got null"
        }
    }
}

private interface DummyInterface

// Private modifier is not supported yet.
internal object DummyObject : DummyInterface