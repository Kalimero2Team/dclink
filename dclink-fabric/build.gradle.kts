plugins {
    id("java")
    id("fabric-loom") version "0.12.+"
}

group = "com.kalimero2.team"
version = "1.0.2-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.quiltmc.org/repository/release")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    minecraft("com.mojang", "minecraft", "1.19")
    mappings(loom.layered {
        officialMojangMappings()
    })
    modImplementation("net.fabricmc","fabric-loader","0.14.6")
    modImplementation("net.fabricmc.fabric-api","fabric-api","0.57.0+1.19")
    modImplementation("net.kyori","adventure-platform-fabric","5.4.0-SNAPSHOT")
    modImplementation("cloud.commandframework","cloud-fabric","1.7.0")

    implementation(project(":dclink-core"))
    include(project(":dclink-core"))
    implementation(project(":dclink-api"))
    include(project(":dclink-core"))

    include("net.kyori","adventure-api","4.11.0")
    include("net.kyori","adventure-text-minimessage","4.11.0")
    include("org.spongepowered","configurate-hocon","4.0.0")
    include("net.dv8tion","JDA","5.0.0-alpha.13") {
        exclude(module= "opus-java")
    }
    include("org.xerial","sqlite-jdbc","3.36.0.3")
}
