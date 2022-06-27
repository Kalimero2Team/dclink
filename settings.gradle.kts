pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "dclink"
include("dclink-paper")
include("dclink-api")
include("dclink-core")
include("dclink-fabric")
