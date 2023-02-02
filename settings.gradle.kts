dependencyResolutionManagement{
    versionCatalogs{
        create("libs"){
            // Core
            plugin("shadow","com.github.johnrengelman.shadow").version("7.1.2")

            version("jda", "5.0.0-beta.3")
            version("configurate-hocon", "4.1.2")
            version("sqlite","3.40.1.0")
            version("adventure","4.12.0")
            version("floodgate-api","2.0-SNAPSHOT")
            version("cloud", "1.8.0")

            library("jda","net.dv8tion","JDA").versionRef("jda")
            library("configurate-hocon","org.spongepowered","configurate-hocon").versionRef("configurate-hocon")
            library("sqlite","org.xerial","sqlite-jdbc").versionRef("sqlite")
            library("adventure-api","net.kyori","adventure-api").versionRef("adventure")
            library("adventure-minimessage","net.kyori","adventure-text-minimessage").versionRef("adventure")
            library("floodgate-api","org.geysermc.floodgate","api").versionRef("floodgate-api")
            library("cloud-core","cloud.commandframework","cloud-core").versionRef("cloud")
            library("cloud-brigadier","cloud.commandframework","cloud-brigadier").versionRef("cloud")

            // Spigot
            plugin("plugin-yml","net.minecrell.plugin-yml.bukkit").version("0.5.2")
            version("spigot-api","1.19.3-R0.1-SNAPSHOT")
            version("adventure-platform","4.2.0")
            version("commodore","2.2")

            library("spigot-api","org.spigotmc","spigot-api").versionRef("spigot-api")
            library("adventure-bukkit","net.kyori","adventure-platform-bukkit").versionRef("adventure-platform")
            library("cloud-bukkit","cloud.commandframework","cloud-bukkit").versionRef("cloud")
            library("commodore","me.lucko","commodore").versionRef("commodore")


            // Paper
            plugin("paper-userdev","io.papermc.paperweight.userdev").version("1.4.0")
            plugin("paper-run","xyz.jpenilla.run-paper").version("2.0.1")

            version("paper-api","1.19.3-R0.1-SNAPSHOT")

            library("cloud-paper","cloud.commandframework","cloud-paper").versionRef("cloud")


            // Velocity
            version("velocity-api","3.1.1")

            library("velocity-api","com.velocitypowered","velocity-api").versionRef("velocity-api")
            library("cloud-velocity","cloud.commandframework","cloud-velocity").versionRef("cloud")


            // Fabric
            plugin("fabric-loom","fabric-loom").version("1.0-SNAPSHOT")

            version("minecraft","1.19.3")
            version("fabric-loader","0.14.11")
            version("fabric-api","0.72.0+1.19.3")
            version("adventure-platform-fabric","5.6.1")

            library("fabric-loader","net.fabricmc","fabric-loader").versionRef("fabric-loader")
            library("fabric-api","net.fabricmc.fabric-api","fabric-api").versionRef("fabric-api")
            library("fabric-api-deprecated","net.fabricmc.fabric-api","fabric-api-deprecated").versionRef("fabric-api")
            library("adventure-fabric","net.kyori","adventure-platform-fabric").versionRef("adventure-platform-fabric")
            library("cloud-fabric","cloud.commandframework","cloud-fabric").versionRef("cloud")
        }
    }
}

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.quiltmc.org/repository/release")
    }
}

rootProject.name = "dclink"

include("dclink-api")
include("dclink-core")
include("dclink-paper")
include("dclink-velocity")
include("dclink-fabric")
