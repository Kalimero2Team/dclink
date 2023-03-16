
plugins {
    alias(libs.plugins.fabric.loom)
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    minecraft("com.mojang", "minecraft", libs.versions.minecraft.get())
    mappings(loom.layered {
        officialMojangMappings()
    })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    modImplementation(libs.adventure.fabric)
    modImplementation(libs.cloud.fabric)

    implementation(project(":dclink-core"))
    include(project(":dclink-core"))
    implementation(project(":dclink-api"))
    include(project(":dclink-core"))

    include(libs.adventure.api)
    include(libs.adventure.minimessage)
    include(libs.configurate.hocon)
    include(libs.jda) {
        exclude(module = "opus-java")
    }
    include(libs.sqlite)
}
