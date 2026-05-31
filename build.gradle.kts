import org.gradle.api.JavaVersion.VERSION_21
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.typeflows)
    alias(libs.plugins.versionCatalogUpdate)
    java
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")

    repositories {
        mavenLocal()
        mavenCentral()
    }

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
            compilerOptions {
                allWarningsAsErrors = false
                jvmTarget.set(JVM_21)
                freeCompilerArgs.add("-jvm-default=enable")
            }
        }

        withType<Test> {
            useJUnitPlatform()
        }

        java {
            sourceCompatibility = VERSION_21
            targetCompatibility = VERSION_21
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    failOnNoDiscoveredTests = false
}

dependencies {
    typeflowsApi(libs.typeflowsGithub)
    typeflowsApi(libs.typeflowsGithubMarketplace)
    typeflowsApi(libs.http4kStandards)
}
