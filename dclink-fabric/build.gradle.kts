plugins {
    alias(libs.plugins.fabric.loom)
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    minecraft("com.mojang", "minecraft", stonecutter.current.version)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric-api")}")
    modImplementation("net.kyori:adventure-platform-fabric:${property("deps.adventure-platform-fabric")}")
    modImplementation("org.incendo:cloud-fabric:${property("deps.cloud-fabric")}")

    implementation(project(":dclink-core"))
    include(project(":dclink-core"))
    implementation(project(":dclink-api"))
    include(project(":dclink-core"))
    implementation(project(":dclink-minecraft")){
        exclude("*", "*")
    }
    include(project(":dclink-minecraft")){
        exclude("*", "*")
    }

    include(libs.adventure.api)
    include(libs.adventure.minimessage)
    include(libs.configurate.hocon)
    include(libs.jda) {
        exclude(module = "opus-java")
    }
    include(libs.sqlite)
}

loom {
    runConfigs.all {
        ideConfigGenerated(true)
    }
}

tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version,
                "minecraftVersion" to stonecutter.current.version,
                "loaderVersion" to libs.versions.fabric.loader.get(),
            )
        }
    }
}