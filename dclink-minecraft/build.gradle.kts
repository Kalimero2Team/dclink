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
    implementation(project(":dclink-core"))
    implementation(libs.cloud.brigadier)
    implementation(libs.brigadier)
}