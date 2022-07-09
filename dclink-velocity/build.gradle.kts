plugins {
    id("java")
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
    compileOnly("com.velocitypowered","velocity-api", "3.1.1")
    annotationProcessor("com.velocitypowered","velocity-api", "3.1.1")

    implementation("net.kyori","adventure-text-minimessage","4.11.0")
    implementation("cloud.commandframework","cloud-velocity","1.7.0")
    implementation(project(":dclink-api"))
    implementation(project(":dclink-core"))
}

tasks{
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar{
        fun reloc(pkg: String) = relocate(pkg, "com.kalimero2.team.dclink.libs.cloud.dependency.$pkg")
        reloc("cloud.commandframework")
        reloc("io.leangen")
        reloc("net.dv8tion")
        reloc("org.xerial")
    }
    processResources {
        filesMatching(listOf("velocity-plugin.json")) {
            expand(
                "id" to "dclink-velocity",
                "name" to "DCLink",
                "version" to project.version,
                "description" to project.description,
                "url" to "https://github.com/Kalimero2Team/dclink",
                "author" to "byquanton"
            )
        }
    }
}