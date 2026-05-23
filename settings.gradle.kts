pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://jitpack.io")
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.essential.gg/repository/maven-public")
    }
    plugins {
        id("gg.essential.multi-version.root") version "0.7.0-alpha.4"
    }
}

val versions = listOf(
    "1.21",
    "1.21.2",
    "1.21.4",
    "1.21.5",
    "1.21.7",
    "1.21.10",
    "1.21.11",
    "26.1"
)

rootProject.buildFileName = "root.gradle.kts"

versions.forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}
