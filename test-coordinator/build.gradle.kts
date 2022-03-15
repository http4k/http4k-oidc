import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType

apply(plugin = "com.adarshr.test-logger")

dependencies {
    implementation("org.http4k:http4k-client-okhttp:4.19.0.0")
    implementation("org.http4k:http4k-core:4.19.0.0")
    implementation("org.http4k:http4k-format-jackson:4.19.0.0")
    implementation("org.http4k:http4k-cloudnative:4.19.0.0")
    implementation("org.http4k:http4k-security-oauth:4.19.0.0")
    implementation("org.http4k:http4k-server-undertow:4.19.0.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")
    implementation("dev.forkhandles:values4k:2.0.0.0")
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    implementation("com.nimbusds:nimbus-jose-jwt:9.20")
    testImplementation("org.http4k:http4k-testing-hamkrest:4.19.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
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

tasks.register<Test>("conformanceTests") {
    description = "Runs OIDC Foundation Conformance tests."
    group = "verification"

    useJUnitPlatform()

    filter {
        include("conformance/**")
    }
}