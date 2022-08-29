package conformance.authserver.core

import conformance.assertPassed
import org.http4k.*
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.uri
import org.http4k.lens.value
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.fail
import java.time.Duration

class ConformanceTests {
    private val environment = Environment.ENV
    private val apiToken = EnvironmentKey.value(ApiToken).required("CONFORMANCE_API_TOKEN")
    private val planIdKey = EnvironmentKey.value(PlanId).required("CONFORMANCE_PLAN_ID")
    private val baseUriKey = EnvironmentKey.uri().required("CONFORMANCE_RELYING_PARTY_BASE_URI")

    private val conformance = Conformance(apiToken(environment))
    private val planId = planIdKey(environment)
    private val baseUri = baseUriKey(environment)

    @TestFactory
    fun `execute test plan`(): List<DynamicTest> {
        val testsToRun = listOf<TestName>(
//            TestName.of("oidcc-server"),
        )

        val tests = conformance.fetchAvailableTests().filter { it.testName in testsToRun }

        return tests.map { testDefinition ->
            DynamicTest.dynamicTest(testDefinition.displayName.value) {
                val testInfo = conformance.createTestFromPlan(planId, testDefinition.testName)
                runTest(testInfo)
            }
        }
    }

    private fun runTest(testInfo: TestInfo) {
        conformance.waitForStatus(testInfo.testId, testInfo.testName, TestStatus.FINISHED, Duration.ofSeconds(7)) {
            fail("Timed out waiting for test to finish. Full logs: ${testInfo.logs}\n")
        }.assertPassed()
    }
}