plugins {
    id("java")
}

group = "com.kalimero2.team"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":dclink-core"))
}