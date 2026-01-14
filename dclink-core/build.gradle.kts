plugins {
    java
}

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":dclink-api"))
    implementation(libs.adventure.api)
    implementation(libs.adventure.minimessage)
    implementation(libs.cloud.core)
    implementation(libs.configurate.hocon)
    implementation(libs.jda) {
        exclude(module= "opus-java")
        exclude(module= "tink")
    }
    implementation(libs.sqlite)
    compileOnly(libs.floodgate.api)
}