description = "Http4k Security Oauth2 + OpenID Connect support"

dependencies {
    api(platform(Http4k.bom))
    api("org.http4k:http4k-security-core:_")
    implementation("org.http4k:http4k-format-moshi:_") {
        exclude(group = "org.jetbrains.kotlin", module= "kotlin-reflect")
    }
    implementation("dev.forkhandles:result4k:_")
    implementation("commons-codec:commons-codec:_")
    implementation(Http4k.format.jackson)
    testImplementation(Http4k.testing.hamkrest)
    testImplementation(Testing.junit.jupiter.api)
    testImplementation(Testing.junit.jupiter.engine)
}
