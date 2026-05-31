pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "http4k-oidc"

include("oidc")
include("oidc-test-server")
include("test-coordinator")
