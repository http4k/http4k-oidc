description = "Http4k Security Oauth2 + OpenID Connect support"

dependencies {
    api(platform(libs.http4kBom))
    api(libs.http4kSecurityCore)
    implementation(libs.http4kFormatMoshi) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
    }
    implementation(libs.result4k)
    implementation(libs.commonsCodec)
    implementation(libs.http4kFormatJackson)
    testImplementation(libs.http4kTestingHamkrest)
    testImplementation(libs.junitJupiterApi)
    testImplementation(libs.junitJupiterEngine)
}
