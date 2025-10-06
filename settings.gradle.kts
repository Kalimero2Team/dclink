dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            plugin("shadow", "com.gradleup.shadow").version("8.3.8")

            version("jda", "5.3.1")
            version("configurate-hocon", "4.2.0")
            version("sqlite", "3.50.3.0")
            version("adventure", "4.19.0")
            version("floodgate-api", "2.0-SNAPSHOT")
            version("cloud-core", "2.0.0-beta.11")
            version("cloud-modded", "2.0.0-beta.12")
            version("runtask", "2.3.1")
            version("minecraft", "1.21.8")

            library("jda", "net.dv8tion", "JDA").versionRef("jda")
            library("configurate-hocon", "org.spongepowered", "configurate-hocon").versionRef("configurate-hocon")
            library("sqlite", "org.xerial", "sqlite-jdbc").versionRef("sqlite")
            library("adventure-api", "net.kyori", "adventure-api").versionRef("adventure")
            library("adventure-minimessage", "net.kyori", "adventure-text-minimessage").versionRef("adventure")
            library("floodgate-api", "org.geysermc.floodgate", "api").versionRef("floodgate-api")
            library("cloud-core", "org.incendo", "cloud-core").versionRef("cloud-core")
            library("cloud-brigadier", "org.incendo", "cloud-brigadier").versionRef("cloud-core")

            // Paper
            plugin("paper-run", "xyz.jpenilla.run-paper").versionRef("runtask")
            plugin("plugin-yml", "net.minecrell.plugin-yml.paper").version("0.6.0")

            version("paper-api", "1.21.8-R0.1-SNAPSHOT")

            library("paper-api", "io.papermc.paper", "paper-api").versionRef("paper-api")
            library("cloud-paper", "org.incendo", "cloud-paper").versionRef("cloud-core")


            // Velocity
            plugin("velocity-run", "xyz.jpenilla.run-velocity").versionRef("runtask")

            version("velocity-api", "3.4.0-SNAPSHOT")

            library("velocity-api", "com.velocitypowered", "velocity-api").versionRef("velocity-api")
            library("cloud-velocity", "org.incendo", "cloud-velocity").versionRef("cloud-core")


            // Fabric
            plugin("fabric-loom", "fabric-loom").version("1.11.4")
            version("fabric-loader", "0.16.10")
            version("fabric-api", "0.130.0+1.21.8")
            version("adventure-platform-fabric", "6.5.1")

            library("fabric-loader", "net.fabricmc", "fabric-loader").versionRef("fabric-loader")
            library("fabric-api", "net.fabricmc.fabric-api", "fabric-api").versionRef("fabric-api")
            library("adventure-fabric", "net.kyori", "adventure-platform-fabric").versionRef("adventure-platform-fabric")
            library("cloud-fabric", "org.incendo", "cloud-fabric").versionRef("cloud-modded")


            // NeoForge
            plugin("neoforge-moddev", "net.neoforged.moddev").version("2.0.105")
            version("neoforge", "21.8.17")
            version("adventure-platform-neoforge", "6.7.0")

            library("cloud-neoforge", "org.incendo", "cloud-neoforge").versionRef("cloud-modded")
            library("adventure-neoforge", "net.kyori", "adventure-platform-neoforge").versionRef("adventure-platform-neoforge")
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

rootProject.name = "dclink"

include("dclink-api")
include("dclink-core")
include("dclink-paper")
include("dclink-velocity")
include("dclink-fabric")
include("dclink-neoforge")
