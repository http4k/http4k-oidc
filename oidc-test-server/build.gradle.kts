plugins {
    application
}

application {
    mainClass = "org.http4k.ServerKt"
}

dependencies {
    api(platform(Http4k.bom))
    implementation(Http4k.client.okhttp)
    implementation(Http4k.core)
    implementation(Http4k.format.jackson)
    implementation("org.http4k:http4k-config")
    implementation(Http4k.securityOauth)
//    implementation(project(":oidc"))
    implementation("dev.forkhandles:result4k:_")
    implementation(Http4k.server.undertow)
    implementation(Kotlin.stdlib.jdk8)
    implementation("dev.forkhandles:values4k:_")
    implementation("io.jsonwebtoken:jjwt-api:_")
    implementation("com.nimbusds:nimbus-jose-jwt:_")
    implementation("org.bouncycastle:bcpkix-jdk15on:_")
    testImplementation(Http4k.testing.hamkrest)

    testApi(platform("org.junit:junit-bom:_"))
    testApi("org.junit.platform:junit-platform-launcher")
    testApi("org.junit.jupiter:junit-jupiter-api")
    testApi("org.junit.jupiter:junit-jupiter-engine")
}
