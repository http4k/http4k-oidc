package org.http4k

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.ValueFactory
import org.http4k.ConformanceJackson.auto
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.filter.ClientFilters
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.inIntelliJOnly
import org.http4k.format.*
import org.http4k.lens.BiDiMapping
import org.http4k.lens.value

fun main() {
    val environment = Environment.ENV
    val apiToken = EnvironmentKey.value(ApiToken).required("CONFORMANCE_API_TOKEN")

    val conformance = Conformance(apiToken(environment))

    val id = conformance.createTestFromPlan(PlanId.of("6aJ57GEWsAPJk"), TestName.of("oidcc-client-test"))
    println(conformance.getTestInfo(id))
    ClientInteractions().performBasicOauth()
    println(conformance.getTestInfo(id))
}

class Conformance(apiToken: ApiToken) {
    private val client = Filter.NoOp.then(ClientFilters.SetBaseUriFrom(Uri.of("https://www.certification.openid.net")))
        .then(DebuggingFilters.PrintRequestAndResponse().inIntelliJOnly())
        .then(ClientFilters.BearerAuth(apiToken.value))
        .then(Filter { next -> { next(it.header("Content-Type", "application/json")) } }).then(JavaHttpClient())

    fun createTestFromPlan(planId: PlanId, testName: TestName) =
        testId(client(Request(POST, "/api/runner").query("plan", planId.value).query("test", testName.value)))

    fun getTestInfo(testId: TestId) = testInfo(client(Request(GET, "/api/info/${testId.value}")))

    companion object {
        val testId = Body.auto<TestCreationResponse>().map(TestCreationResponse::id).toLens()
        val testInfo = Body.auto<TestInfo>().toLens()
    }
}

class ClientInteractions {
    private val client = Filter.NoOp.then(ClientFilters.FollowRedirects()).then(ClientFilters.Cookies())
        .then(DebuggingFilters.PrintRequestAndResponse().inIntelliJOnly()).then(JavaHttpClient())

    fun performBasicOauth() {
        client(Request(GET, "https://http4k-oidc.herokuapp.com/oauth"))
    }
}

class PlanId private constructor(value: String) : StringValue(value) {
    companion object : ValueFactory<PlanId, String>(::PlanId, null, { it })
}

class TestName private constructor(value: String) : StringValue(value) {
    companion object : ValueFactory<TestName, String>(::TestName, null, { it })
}

class ApiToken private constructor(value: String) : StringValue(value) {
    companion object : ValueFactory<ApiToken, String>(::ApiToken, null, { it })
}

class TestId private constructor(value: String) : StringValue(value) {
    companion object : ValueFactory<TestId, String>(::TestId, null, { it })
}

enum class TestStatus {
    CREATED, WAITING, INTERRUPTED, FINISHED
}

enum class TestResult {
    PASSED, FAILED
}

data class TestCreationResponse(val id: TestId)

data class TestInfo(val status: TestStatus, val result: TestResult?)

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
    text(BiDiMapping({ TestStatus.valueOf(it) }, TestStatus::name))
    text(BiDiMapping({ TestResult.valueOf(it) }, TestResult::name))
}