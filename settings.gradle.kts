pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.quiltmc.org/repository/release")
        maven("https://maven.fabricmc.net/")
    }
}

rootProject.name = "dclink"
include("dclink-api")
include("dclink-core")
include("dclink-paper")
