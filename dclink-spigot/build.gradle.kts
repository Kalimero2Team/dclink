import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    @Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
    alias(libs.plugins.plugin.yml)
    @Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

dependencies {
    compileOnly(libs.spigot.api)
    bukkitLibrary(libs.adventure.bukkit)
    bukkitLibrary(libs.adventure.minimessage)
    bukkitLibrary(libs.commandframework.bukkit)
    bukkitLibrary(libs.configurate.hocon)
    bukkitLibrary(libs.jda)
    bukkitLibrary(libs.sqlite)
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