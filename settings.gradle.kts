plugins {
    id("de.fayard.refreshVersions") version "0.40.1"
}

rootProject.name = "http4k-oidc"

include("oidc")
include("relying-party")
include("test-coordinator")
