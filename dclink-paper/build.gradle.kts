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

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
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
