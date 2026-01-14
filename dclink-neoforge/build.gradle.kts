plugins {
    java
    alias(libs.plugins.neoforge.moddev)
}

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation(project(":dclink-core"))
    jarJar(project(":dclink-core"))
    implementation(project(":dclink-api"))
    jarJar(project(":dclink-api"))
    implementation(project(":dclink-minecraft")){
        exclude("*", "*")
    }
    jarJar(project(":dclink-minecraft")){
        exclude("*", "*")
    }

    implementation("net.kyori:adventure-platform-neoforge:${property("deps.adventure-platform-neoforge")}")
    jarJar("net.kyori:adventure-platform-neoforge:${property("deps.adventure-platform-neoforge")}")
    implementation("org.incendo:cloud-neoforge:${property("deps.cloud-neoforge")}")
    jarJar("org.incendo:cloud-neoforge:${property("deps.cloud-neoforge")}")

    implementation(libs.adventure.api)
    jarJar(libs.adventure.api)
    implementation(libs.adventure.minimessage)
    jarJar(libs.adventure.minimessage)
    implementation(libs.configurate.hocon)
    jarJar(libs.configurate.hocon)

    implementation(libs.jda) {
        exclude(module = "opus-java")
        exclude(module = "slf4j-api")
    }
    jarJar(libs.jda)
    implementation(libs.sqlite)
    jarJar(libs.sqlite)
}


neoForge {
    version = property("deps.neoforge").toString()

    mods {
        create("dclink_neoforge") {
            sourceSet(sourceSets["main"])
        }
    }

    runs {
        create("server") {
            server()
            gameDirectory = file("run")
            programArguments.addAll("nogui")
        }
    }
}


tasks {
    processResources {
        filesMatching("neoforge.mods.toml") {
            expand(
                "version" to project.version,
                "minecraftVersion" to stonecutter.current.version
            )
        }
    }
}