package io.cloudx.sdk

import io.mockk.junit4.MockKRule
import org.junit.Rule

open class MockkTest {

    @get:Rule
    val mockkRule = MockKRule(this)
}