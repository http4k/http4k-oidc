plugins {
    id("de.fayard.refreshVersions") version "0.51.0"
}

rootProject.name = "http4k-oidc"

include("oidc")
include("oidc-test-server")
include("test-coordinator")
