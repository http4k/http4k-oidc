plugins {
    id("de.fayard.refreshVersions") version "0.50.1"
}

rootProject.name = "http4k-oidc"

include("oidc")
include("oidc-test-server")
include("test-coordinator")
