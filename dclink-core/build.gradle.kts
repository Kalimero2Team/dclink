plugins {
    id("java")
}

group = "com.kalimero2.team"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {
    implementation(project(":dclink-api"))
    implementation("org.spongepowered","configurate-yaml","4.0.0")
    implementation("net.dv8tion","JDA","5.0.0-alpha.12") {
        exclude(module= "opus-java")
    }
    implementation("org.knowm","yank", "3.4.0")
}