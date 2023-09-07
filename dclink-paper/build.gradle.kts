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
    paperLibrary(libs.cloud.paper)
    paperLibrary(libs.configurate.hocon)
    paperLibrary(libs.jda)
    paperLibrary(libs.sqlite)
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

    paper {
        load = BukkitPluginDescription.PluginLoadOrder.STARTUP
        main = "com.kalimero2.team.dclink.paper.PaperPlugin"
        loader = "com.kalimero2.team.dclink.paper.PaperLibrariesLoader"
        generateLibrariesJson = true
        foliaSupported = true
        description = project.description
        apiVersion = "1.19"
        authors = listOf("byquanton")

        serverDependencies {
            register("floodgate") {
                required = false
            }
        }
    }
}
