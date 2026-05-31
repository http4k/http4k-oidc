import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
    alias(libs.plugins.testLogger)
}

dependencies {
    api(platform(libs.http4kBom))
    implementation(libs.http4kClientOkhttp)
    implementation(libs.http4kCore)
    implementation(libs.http4kFormatJackson)
    implementation(libs.http4kConfig)
    implementation(libs.http4kSecurityOauth)
    implementation(libs.http4kServerUndertow)
    implementation(libs.kotlinStdlibJdk8)
    implementation(libs.values4k)
    implementation(libs.jjwtApi)
    implementation(libs.nimbusJoseJwt)
    testImplementation(libs.http4kTestingHamkrest)
    testApi(platform(libs.junitBom))
    testApi(libs.junitPlatformLauncher)
    testApi(libs.junitJupiterApi)
    testApi(libs.junitJupiterEngine)
}

tasks.test {
    filter {
        exclude("conformance/**")
        isFailOnNoMatchingTests = false
    }
    failOnNoDiscoveredTests = false
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
    showStandardStreams = true
    showPassedStandardStreams = true
    showSkippedStandardStreams = true
    showFailedStandardStreams = true
    logLevel = LogLevel.LIFECYCLE
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
