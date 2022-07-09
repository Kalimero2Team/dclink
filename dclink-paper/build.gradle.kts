import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.3.7"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.kalimero2.team"
version = "1.0.2-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

dependencies {
    paperDevBundle("1.19-R0.1-SNAPSHOT")
    bukkitLibrary("cloud.commandframework","cloud-paper","1.7.0")
    bukkitLibrary("org.spongepowered","configurate-hocon","4.0.0")
    bukkitLibrary("net.dv8tion","JDA","5.0.0-alpha.13")
    bukkitLibrary("org.xerial","sqlite-jdbc","3.36.0.3")
    compileOnly("org.geysermc.floodgate","api","2.0-SNAPSHOT")
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