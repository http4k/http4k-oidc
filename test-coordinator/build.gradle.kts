import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType

apply(plugin = "com.adarshr.test-logger")

dependencies {
    api(platform(Http4k.bom))
    implementation(Http4k.client.okhttp)
    implementation(Http4k.core)
    implementation(Http4k.format.jackson)
    implementation("org.http4k:http4k-config")
    implementation(Http4k.securityOauth)
    implementation(Http4k.server.undertow)
    implementation(Kotlin.stdlib.jdk8)
    implementation("dev.forkhandles:values4k:_")
    implementation("io.jsonwebtoken:jjwt-api:_")
    implementation("com.nimbusds:nimbus-jose-jwt:_")
    testImplementation(Http4k.testing.hamkrest)
    testImplementation(Testing.junit.jupiter.api)
    testImplementation(Testing.junit.jupiter.engine)
}

tasks.test {
    filter {
        exclude("conformance/**")
    }
}

configure<TestLoggerExtension> {
    theme = ThemeType.STANDARD
    showExceptions = true
    showStackTraces = true
    showFullStackTraces = false
    showCauses = true
    slowThreshold = 2000
    showSummary = true
    showSimpleNames = false
    showPassed = true
    showSkipped = true
    showFailed = true
    showOnlySlow = false
    showStandardStreams = false
    showPassedStandardStreams = true
    showSkippedStandardStreams = true
    showFailedStandardStreams = true
    logLevel =  LogLevel.LIFECYCLE
}

tasks.register<Test>("conformanceTestsRelyingParty") {
    description = "Runs OIDC Foundation Conformance tests (Relying Party)."
    group = "verification"

    useJUnitPlatform()

    filter {
        include("conformance/relyingparty/core/**")
    }
}

tasks.register<Test>("conformanceTestsAuthServer") {
    description = "Runs OIDC Foundation Conformance tests (Auth Server)."
    group = "verification"

    useJUnitPlatform()

    filter {
        include("conformance/authserver/core/**")
    }
}