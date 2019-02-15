package com.thepeaklab.backendindependentuitests.core.mock

data class Expectation(val success: (() -> MockSuccess)? = null,
                       val failure: (() -> MockFailure)? = null,
                       val value: Any? = null,
                       val directReturn: Boolean = false)
data class MockSuccess(val result: Any)
data class MockFailure(val error: String)