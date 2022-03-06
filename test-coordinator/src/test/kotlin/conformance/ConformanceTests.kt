package conformance

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.*
import org.http4k.TestResult.PASSED
import org.http4k.TestStatus.FINISHED
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.value
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class ConformanceTests {

    private val environment = Environment.ENV
    private val apiToken = EnvironmentKey.value(ApiToken).required("CONFORMANCE_API_TOKEN")

    private val conformance = Conformance(apiToken(environment))

    @TestFactory
    fun `execute test plan`(): List<DynamicTest> {
        val testInfo = conformance.createTestFromPlan(PlanId.of("6aJ57GEWsAPJk"), TestName.of("oidcc-client-test"))
        return listOf(DynamicTest.dynamicTest(testInfo.summary) { runTest(testInfo) })
    }

    private fun runTest(testInfo: TestInfo) {
        ClientInteractions().performBasicOauth()

        conformance.getTestInfo(testInfo.testId, testInfo.testName).assertPassed()
    }
}

private fun TestInfo.assertPassed() {
    assertThat("Unexpected test status. Full logs: $logs\n", status, equalTo(FINISHED))
    assertThat("Unexpected test result. Full logs: $logs\n", result, equalTo(PASSED))
}
