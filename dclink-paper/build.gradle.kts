import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
plugins {
    java
    alias(libs.plugins.paper.run)
    alias(libs.plugins.plugin.yml)
    alias(libs.plugins.shadow)
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    compileOnly(libs.paper.api)
    paperLibrary(libs.cloud.paper)
    paperLibrary(libs.configurate.hocon)
    paperLibrary(libs.jda)
    paperLibrary(libs.sqlite)
    implementation(project(":dclink-api"))
    implementation(project(":dclink-core")) {
        exclude("*", "*") // Excludes all dependencies of dclink-core because they are put into the plugin.yml file
    }
    implementation(project(":dclink-minecraft")) {
        exclude("*", "*")
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
        downloadPlugins {
            // Geyser (in this case Thermalquelle)
            github("Kalimero2Team", "Thermalquelle","latest-dev", "Geyser-Spigot.jar")
            // Floodgate
            url("https://download.geysermc.org/v2/projects/floodgate/versions/latest/builds/latest/downloads/spigot")
        }

        minecraftVersion(libs.versions.minecraft.get())
    }

    paper {
        load = BukkitPluginDescription.PluginLoadOrder.STARTUP
        main = "com.kalimero2.team.dclink.paper.PaperPlugin"
        loader = "com.kalimero2.team.dclink.paper.PaperLibrariesLoader"
        generateLibrariesJson = true
        foliaSupported = true
        description = project.description
        apiVersion = "1.20.5"
        authors = listOf("byquanton")

        serverDependencies {
            register("floodgate") {
                required = false
            }
        }
    }
}
