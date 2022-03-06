package org.http4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.TestResult.PASSED
import org.http4k.TestStatus.FINISHED
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.value
import org.junit.jupiter.api.Test

class ConformanceTests {

    @Test
    fun `run core test`() {
        val environment = Environment.ENV
        val apiToken = EnvironmentKey.value(ApiToken).required("CONFORMANCE_API_TOKEN")

        val conformance = Conformance(apiToken(environment))

        val testInfo = conformance.createTestFromPlan(PlanId.of("6aJ57GEWsAPJk"), TestName.of("oidcc-client-test"))

        ClientInteractions().performBasicOauth()

        conformance.getTestInfo(testInfo.testId, testInfo.testName).assertPassed()
    }
}

private fun TestInfo.assertPassed() {
    assertThat("Unexpected test status. Full logs: $logs\n", status, equalTo(FINISHED))
    assertThat("Unexpected test result. Full logs: $logs\n", result, equalTo(PASSED))
}
