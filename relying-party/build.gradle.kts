plugins {
    application
}

application {
    mainClassName = "org.http4k.ServerKt"
}

dependencies {
    api(platform(Http4k.bom))
    implementation(Http4k.client.okhttp)
    implementation(Http4k.core)
    implementation(Http4k.format.jackson)
    implementation(Http4k.cloudnative)
    implementation(project(":oidc"))
    implementation("dev.forkhandles:result4k:_")
    implementation(Http4k.server.undertow)
    implementation(Kotlin.stdlib.jdk8)
    implementation("dev.forkhandles:values4k:_")
    implementation("io.jsonwebtoken:jjwt-api:_")
    implementation("com.nimbusds:nimbus-jose-jwt:_")
    testImplementation(Http4k.testing.hamkrest)
    testImplementation(Testing.junit.jupiter.api)
    testImplementation(Testing.junit.jupiter.engine)
}
