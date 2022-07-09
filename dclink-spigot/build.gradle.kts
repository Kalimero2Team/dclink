import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.kalimero2.team"
version = "1.0.2-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

dependencies {
    compileOnly("org.spigotmc","spigot-api","1.19-R0.1-SNAPSHOT")
    bukkitLibrary("net.kyori","adventure-platform-bukkit","4.1.1")
    bukkitLibrary("net.kyori","adventure-text-minimessage","4.11.0")
    bukkitLibrary("cloud.commandframework","cloud-bukkit","1.7.0")
    bukkitLibrary("org.spongepowered","configurate-hocon","4.0.0")
    bukkitLibrary("net.dv8tion","JDA","5.0.0-alpha.13")
    bukkitLibrary("org.xerial","sqlite-jdbc","3.36.0.3")
    implementation(project(":dclink-api"))
    implementation(project(":dclink-core")){
        exclude("*", "*") // Excludes all dependencies of dclink-core because they are put into the plugin.yml file
    }
}

bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "com.kalimero2.team.dclink.spigot.SpigotPlugin"
    apiVersion = "1.19"
    authors = listOf("byquanton")
    softDepend = listOf("floodgate")
}