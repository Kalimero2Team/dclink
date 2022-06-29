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
    implementation("net.kyori","adventure-api","4.11.0")
    implementation("cloud.commandframework","cloud-core","1.7.0")
    implementation("cloud.commandframework","cloud-annotations","1.7.0")

    implementation("org.spongepowered","configurate-hocon","4.0.0")
    implementation("net.dv8tion","JDA","5.0.0-alpha.12") {
        exclude(module= "opus-java")
    }
    implementation("org.xerial","sqlite-jdbc","3.36.0.3")
}