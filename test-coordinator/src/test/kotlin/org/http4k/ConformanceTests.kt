package org.http4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
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

        val id = conformance.createTestFromPlan(PlanId.of("6aJ57GEWsAPJk"), TestName.of("oidcc-client-test"))

        ClientInteractions().performBasicOauth()

        conformance.getTestInfo(id).assertPassed()
    }
}

private fun TestInfo.assertPassed() {
    assertThat(status, equalTo(TestStatus.FINISHED))
    assertThat(result, equalTo(TestResult.PASSED))
}
