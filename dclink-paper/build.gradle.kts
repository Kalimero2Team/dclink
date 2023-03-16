import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    alias(libs.plugins.paper.run)
    alias(libs.plugins.plugin.yml)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

dependencies {
    compileOnly(libs.paper.api)
    bukkitLibrary(libs.cloud.paper)
    bukkitLibrary(libs.configurate.hocon)
    bukkitLibrary(libs.jda)
    bukkitLibrary(libs.sqlite)
    implementation(project(":dclink-api"))
    implementation(project(":dclink-core")) {
        exclude("*", "*") // Excludes all dependencies of dclink-core because they are put into the plugin.yml file
    }
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
    }

    runServer {
        minecraftVersion("1.19.4")
    }

    bukkit {
        load = BukkitPluginDescription.PluginLoadOrder.STARTUP
        main = "com.kalimero2.team.dclink.paper.PaperPlugin"
        description = project.description
        apiVersion = "1.19"
        authors = listOf("byquanton")
        softDepend = listOf("floodgate")
    }
}
