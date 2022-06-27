plugins {
    id("java")
    id("org.quiltmc.loom") version "0.12.+"
}

group = "com.kalimero2.team"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.quiltmc.org/repository/release")
}

dependencies {
    minecraft("com.mojang", "minecraft", "1.19")
    mappings(loom.layered {
        addLayer(quiltMappings.mappings("org.quiltmc:quilt-mappings:1.19+build.1:v2"))
        // officialMojangMappings()
    })
    modImplementation("org.quiltmc:quilt-loader:0.17.1-beta.4")
    modImplementation("org.quiltmc.quilted-fabric-api:quilted-fabric-api:2.0.0-beta.1+0.56.3-1.19")

    implementation(project(":dclink-core"))
    implementation(project(":dclink-api"))
}