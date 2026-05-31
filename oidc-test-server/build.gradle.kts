plugins {
    application
}

application {
    mainClass = "org.http4k.ServerKt"
}

dependencies {
    api(platform(libs.http4kBom))
    implementation(libs.http4kClientOkhttp)
    implementation(libs.http4kCore)
    implementation(libs.http4kFormatJackson)
    implementation(libs.http4kConfig)
    implementation(libs.http4kSecurityOauth)
    implementation(libs.result4k)
    implementation(libs.http4kServerUndertow)
    implementation(libs.kotlinStdlibJdk8)
    implementation(libs.values4k)
    implementation(libs.jjwtApi)
    implementation(libs.nimbusJoseJwt)
    implementation(libs.bcpkixJdk18on)
    testImplementation(libs.http4kTestingHamkrest)

    testApi(platform(libs.junitBom))
    testApi(libs.junitPlatformLauncher)
    testApi(libs.junitJupiterApi)
    testApi(libs.junitJupiterEngine)
}
