package conformance

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.*
import org.http4k.TestResult.PASSED
import org.http4k.TestResult.WARNING
import org.http4k.TestStatus.FINISHED

fun TestInfo.assertPassed() {
    assertThat("Unexpected test status. Full logs: $logs\n", status, equalTo(FINISHED))
    assertThat("Unexpected test result. Full logs: $logs\n", result, equalTo(PASSED))
}

fun TestInfo.assertPassedWithWarning() {
    assertThat("Unexpected test status. Full logs: $logs\n", status, equalTo(FINISHED))
    assertThat("Unexpected test result. Full logs: $logs\n", result, equalTo(WARNING))
}
