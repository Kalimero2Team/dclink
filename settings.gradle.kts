dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            plugin("shadow", "com.gradleup.shadow").version("9.3.1")

            version("jda", "6.3.0")
            version("configurate-hocon", "4.2.0")
            version("sqlite", "3.51.1.0")
            version("adventure", "4.26.1")
            version("floodgate-api", "2.0-SNAPSHOT")
            version("runtask", "3.0.2")
            version("minecraft", "1.21.11")

            library("jda", "net.dv8tion", "JDA").versionRef("jda")
            library("configurate-hocon", "org.spongepowered", "configurate-hocon").versionRef("configurate-hocon")
            library("sqlite", "org.xerial", "sqlite-jdbc").versionRef("sqlite")
            library("adventure-api", "net.kyori", "adventure-api").versionRef("adventure")
            library("adventure-minimessage", "net.kyori", "adventure-text-minimessage").versionRef("adventure")
            library("floodgate-api", "org.geysermc.floodgate", "api").versionRef("floodgate-api")
            library("cloud-core", "org.incendo", "cloud-core").version("2.0.0")
            library("brigadier", "com.mojang", "brigadier").version("1.3.10")
            library("cloud-brigadier", "org.incendo", "cloud-brigadier").version("2.0.0-beta.14")

            // Paper
            plugin("paper-run", "xyz.jpenilla.run-paper").versionRef("runtask")
            plugin("plugin-yml", "net.minecrell.plugin-yml.paper").version("0.6.0")

            version("paper-api", "1.21.8-R0.1-SNAPSHOT")

            library("paper-api", "io.papermc.paper", "paper-api").versionRef("paper-api")
            library("cloud-paper", "org.incendo", "cloud-paper").version("2.0.0-beta.14")


            // Velocity
            plugin("velocity-run", "xyz.jpenilla.run-velocity").versionRef("runtask")

            version("velocity-api", "3.4.0-SNAPSHOT")

            library("velocity-api", "com.velocitypowered", "velocity-api").versionRef("velocity-api")
            library("cloud-velocity", "org.incendo", "cloud-velocity").version("2.0.0-beta.14")


            // Fabric
            plugin("fabric-loom", "fabric-loom").version("1.13.3")
            plugin("stonecutter", "dev.kikugie.stonecutter").version("0.8.2")
            version("fabric-loader", "0.18.4")

            library("fabric-loader", "net.fabricmc", "fabric-loader").versionRef("fabric-loader")


            // NeoForge
            plugin("neoforge-moddev", "net.neoforged.moddev").version("2.0.139")


            // Hytale
            library("hytale", "com.hypixel", "Server").version("2026.01.13-50e69c385")
            library("slf4j-api", "org.slf4j", "slf4j-api").version("2.0.17")
        }
    }
}

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases")
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.8.2"
}

rootProject.name = "dclink"

include("dclink-api")
include("dclink-core")
include("dclink-paper")
include("dclink-velocity")
include("dclink-neoforge")
include("dclink-fabric")
include("dclink-hytale")

stonecutter {
    create(project(":dclink-fabric")) {
        versions("1.21.8", "1.21.10", "1.21.11")
        vcsVersion = "1.21.11"
    }
    create(project(":dclink-neoforge")) {
        versions("1.21.8", "1.21.10", "1.21.11")
        vcsVersion = "1.21.11"
    }
}

include("dclink-minecraft")