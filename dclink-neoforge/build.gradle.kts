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

configurations.all {
    resolutionStrategy {
        force("com.fasterxml.jackson.core:jackson-core:2.13.4")
        force("com.fasterxml.jackson.core:jackson-databind:2.13.4")
        force("com.fasterxml.jackson.core:jackson-annotations:2.13.4")
    }
}

dependencies {
    implementation(project(":dclink-core"))
    jarJar(project(":dclink-core"))
    implementation(project(":dclink-api"))
    jarJar(project(":dclink-api"))

    implementation(libs.adventure.neoforge)
    jarJar(libs.adventure.neoforge)
    implementation(libs.cloud.neoforge)
    jarJar(libs.cloud.neoforge)

    implementation(libs.adventure.api)
    jarJar(libs.adventure.api)
    implementation(libs.adventure.minimessage)
    jarJar(libs.adventure.minimessage)
    implementation(libs.configurate.hocon)
    jarJar(libs.configurate.hocon)
    implementation(libs.jda) {
        exclude(module = "opus-java")
    }
    jarJar(libs.jda)
    implementation(libs.sqlite)
    jarJar(libs.sqlite)
}


neoForge {
    version = libs.versions.neoforge.get()

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
                "minecraftVersion" to libs.versions.minecraft.get(),
                "loaderVersion" to libs.versions.fabric.loader.get(),
            )
        }
    }
}