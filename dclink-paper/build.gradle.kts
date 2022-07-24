import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    @Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
    alias(libs.plugins.paper.userdev)
    @Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
    alias(libs.plugins.paper.run)
    @Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
    alias(libs.plugins.plugin.yml)
    @Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

dependencies {
    paperDevBundle(libs.versions.paper.api.get())
    bukkitLibrary(libs.cloud.paper)
    bukkitLibrary(libs.configurate.hocon)
    bukkitLibrary(libs.jda)
    bukkitLibrary(libs.sqlite)
    implementation(project(":dclink-api"))
    implementation(project(":dclink-core")){
        exclude("*", "*") // Excludes all dependencies of dclink-core because they are put into the plugin.yml file
    }
}
tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "com.kalimero2.team.dclink.paper.PaperPlugin"
    apiVersion = "1.19"
    authors = listOf("byquanton")
    softDepend = listOf("floodgate")
}