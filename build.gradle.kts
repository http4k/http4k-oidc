import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
        classpath("com.adarshr:gradle-test-logger-plugin:_")
    }
}

plugins {
    kotlin("jvm")
}

repositories {
    maven { url = uri("libs") }
    mavenCentral()
}

subprojects {
    apply(plugin = "kotlin")

    repositories {
        maven { url = uri("${rootProject.projectDir}/libs") }
        mavenCentral()
    }

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        jvmToolchain(21)
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks {
        withType<Test> {
            useJUnitPlatform()
        }
    }
}