plugins {
    id("java")
    id("org.quiltmc.loom") version "0.12.+"
}

group = "com.kalimero2.team"
version = "1.0.2-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.quiltmc.org/repository/release")
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
}

dependencies {
    minecraft("com.mojang", "minecraft", "1.19")
    mappings(loom.layered {
        addLayer(quiltMappings.mappings("org.quiltmc:quilt-mappings:1.19+build.1:v2"))
        // officialMojangMappings()
    })
    modImplementation("org.quiltmc:quilt-loader:0.17.1-beta.4")
    modImplementation("org.quiltmc.quilted-fabric-api:quilted-fabric-api:2.0.0-beta.1+0.56.3-1.19")
    modImplementation("net.kyori:adventure-platform-fabric:5.4.0-SNAPSHOT")

    implementation(project(":dclink-core"))
    implementation(project(":dclink-api"))
}