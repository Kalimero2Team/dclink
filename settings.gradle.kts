dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // Core
            plugin("shadow", "io.github.goooler.shadow").version("8.1.8") // Fork is required for Java 21

            version("jda", "5.0.2")
            version("configurate-hocon", "4.1.2")
            version("sqlite", "3.46.0.1")
            version("adventure", "4.17.0")
            version("floodgate-api", "2.0-SNAPSHOT")
            version("cloud", "2.0.0-SNAPSHOT")
            version("runtask", "2.3.0")
            version("minecraft", "1.20.6")

            library("jda", "net.dv8tion", "JDA").versionRef("jda")
            library("configurate-hocon", "org.spongepowered", "configurate-hocon").versionRef("configurate-hocon")
            library("sqlite", "org.xerial", "sqlite-jdbc").versionRef("sqlite")
            library("adventure-api", "net.kyori", "adventure-api").versionRef("adventure")
            library("adventure-minimessage", "net.kyori", "adventure-text-minimessage").versionRef("adventure")
            library("floodgate-api", "org.geysermc.floodgate", "api").versionRef("floodgate-api")
            library("cloud-core", "org.incendo", "cloud-core").versionRef("cloud")
            library("cloud-brigadier", "org.incendo", "cloud-brigadier").versionRef("cloud")

            // Paper
            plugin("paper-run", "xyz.jpenilla.run-paper").versionRef("runtask")
            plugin("plugin-yml", "net.minecrell.plugin-yml.paper").version("0.6.0")

            version("paper-api", "1.20.6-R0.1-SNAPSHOT")

            library("paper-api", "io.papermc.paper", "paper-api").versionRef("paper-api")
            library("cloud-paper", "org.incendo", "cloud-paper").version("2.0.0-SNAPSHOT") // beta.7 is required for 1.20.5+


            // Velocity
            plugin("velocity-run", "xyz.jpenilla.run-velocity").versionRef("runtask")

            version("velocity-api", "3.3.0-SNAPSHOT")

            library("velocity-api", "com.velocitypowered", "velocity-api").versionRef("velocity-api")
            library("cloud-velocity", "org.incendo", "cloud-velocity").versionRef("cloud")


            // Fabric
            plugin("fabric-loom", "fabric-loom").version("1.8.6")
            version("fabric-loader", "0.15.10")
            version("fabric-api", "0.99.0+1.20.6")
            version("adventure-platform-fabric", "5.13.0")

            library("fabric-loader", "net.fabricmc", "fabric-loader").versionRef("fabric-loader")
            library("fabric-api", "net.fabricmc.fabric-api", "fabric-api").versionRef("fabric-api")
            library(
                "adventure-fabric",
                "net.kyori",
                "adventure-platform-fabric"
            ).versionRef("adventure-platform-fabric")
            library("cloud-fabric", "org.incendo", "cloud-fabric").versionRef("cloud")
        }
    }
}

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
}

rootProject.name = "dclink"

include("dclink-api")
include("dclink-core")
include("dclink-paper")
include("dclink-velocity")
include("dclink-fabric")
