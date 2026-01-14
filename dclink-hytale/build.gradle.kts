plugins {
    java
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

dependencies {
    compileOnly(files("libs/HytaleServer.jar"))
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.simple)
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
}