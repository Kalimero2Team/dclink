plugins {
    java
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()

    if(System.getenv("REPO_PASSWORD") != null){
        maven {
            name = "kalimero2Repo"
            url = uri("https://repo.kalimero2.com/private")

            credentials {
                username = System.getenv("REPO_USERNAME")
                password = System.getenv("REPO_PASSWORD")
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

dependencies {
    if(System.getenv("REPO_PASSWORD") != null){
        compileOnly("com.hypixel:hytale:2026.01.17-4b0f30090")
    }else {
        compileOnly(files("libs/HytaleServer.jar"))
    }

    implementation(libs.slf4j.api)
    implementation(libs.cloud.core)
    implementation(libs.adventure.minimessage)
    implementation("net.kyori:adventure-text-serializer-plain:4.17.0")
    implementation(project(":dclink-api"))
    implementation(project(":dclink-core")) {
        exclude(
            group = "org.incendo",
            module = "cloud-brigadier"
        )
    }
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")

        fun reloc(pkg: String) = relocate(pkg, "com.kalimero2.team.dclink.libs.$pkg")
        reloc("cloud.commandframework")
        reloc("io.leangen")
        reloc("net.dv8tion")
        reloc("org.xerial")
    }

    processResources {
        filesMatching("manifest.json"){
            expand(
                "version" to project.version,
            )
        }
    }
}