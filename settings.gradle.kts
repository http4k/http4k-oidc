plugins {
    id("de.fayard.refreshVersions") version "0.50.1"
////                            # available:"0.50.2"
}

rootProject.name = "http4k-oidc"

include("oidc")
include("oidc-test-server")
include("test-coordinator")
