package org.http4k

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.ValueFactory
import org.http4k.ConformanceJackson.auto
import org.http4k.client.JavaHttpClient
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.filter.ClientFilters
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.inIntelliJOnly
import org.http4k.format.*
import org.http4k.lens.BiDiMapping
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.TimeoutException


class Conformance(apiToken: ApiToken) {
    private val client = Filter.NoOp.then(ClientFilters.SetBaseUriFrom(Uri.of("https://www.certification.openid.net")))
        .then(DebuggingFilters.PrintRequestAndResponse().inIntelliJOnly())
        .then(ClientFilters.BearerAuth(apiToken.value))
        .then(Filter { next -> { next(it.header("Content-Type", "application/json")) } }).then(JavaHttpClient())

    fun fetchAvailableTests(): List<TestDefinition> =
        availableTestsResponse(client(Request(GET, "/api/runner/available")))

    fun createTestFromPlan(planId: PlanId, testName: TestName, waitUntil: Set<TestStatus>) =
        testId(
            client(
                Request(POST, "/api/runner").query("plan", planId.value).query("test", testName.value)
            )
        ).let {
            waitForStatus(it, testName, waitUntil, Duration.ofSeconds(5))
        }

    fun getTestInfo(testId: TestId, testName: TestName) =
        testInfoResponse(client(Request(GET, "/api/info/${testId.value}")))
            .let { TestInfo(testId, testName, it.summary, it.status, it.result) }

    fun waitForStatus(
        testId: TestId,
        testName: TestName,
        status: TestStatus,
        duration: Duration,
        onTimeout: (e: TimeoutException) -> Nothing = { throw (it) }
    ): TestInfo = waitForStatus(testId, testName, setOf(status), duration, onTimeout)

    private fun waitForStatus(
        testId: TestId,
        testName: TestName,
        statuses: Set<TestStatus>,
        duration: Duration,
        onTimeout: (e: TimeoutException) -> Nothing = { throw (it) }
    ): TestInfo {
        val future = CompletableFuture<TestInfo>()
        val executor = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({
            val currentStatus = getTestInfo(testId, testName)
            if (currentStatus.status in statuses) {
                future.complete(currentStatus)
            }
        }, 0, 1, SECONDS)
        future.thenAccept { executor.cancel(false) }
        try {
            return future.get(duration.toMillis(), MILLISECONDS)
        } catch (e: TimeoutException) {
            onTimeout(e)
        }
    }

    companion object {
        val testId = Body.auto<TestCreationResponse>().map(TestCreationResponse::id).toLens()
        val testInfoResponse = Body.auto<TestInfoResponse>().toLens()
        val availableTestsResponse = Body.auto<List<TestDefinition>>().toLens()
    }
}

class PlanId private constructor(value: String) : StringValue(value) {
    companion object : ValueFactory<PlanId, String>(::PlanId, null, { it })
}

class TestName private constructor(value: String) : StringValue(value) {
    companion object : ValueFactory<TestName, String>(::TestName, null, { it })
}

class DisplayName private constructor(value: String) : StringValue(value) {
    companion object : ValueFactory<DisplayName, String>(::DisplayName, null, { it })
}

class ApiToken private constructor(value: String) : StringValue(value) {
    companion object : ValueFactory<ApiToken, String>(::ApiToken, null, { it })
}

class TestId private constructor(value: String) : StringValue(value) {
    companion object : ValueFactory<TestId, String>(::TestId, null, { it })
}

data class TestDefinition(val testName: TestName, val displayName: DisplayName)

data class TestInfo(
    val testId: TestId,
    val testName: TestName,
    val summary: String,
    val status: TestStatus,
    val result: TestResult?,
    val logs: Uri = Uri.of("https://www.certification.openid.net/log-detail.html?log=${testId.value}")
)

enum class TestStatus {
    CREATED, WAITING, RUNNING, INTERRUPTED, FINISHED
}

enum class TestResult {
    PASSED, FAILED, WARNING
}

data class TestCreationResponse(val id: TestId)

data class TestInfoResponse(val summary: String, val status: TestStatus, val result: TestResult?)

object ConformanceJackson : ConfigurableJackson(
    KotlinModule.Builder().build().asConfigurable().withStandardMappings().withCustomMappings().done()
        .deactivateDefaultTyping().setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
        .configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
)

fun <T> AutoMappingConfiguration<T>.withCustomMappings() = apply {
    value(PlanId)
    value(TestId)
    value(TestName)
    value(DisplayName)
    text(BiDiMapping({ TestStatus.valueOf(it) }, TestStatus::name))
    text(BiDiMapping({ TestResult.valueOf(it) }, TestResult::name))
}