repositories {
    mavenCentral()
    maven("https://repo.opencollab.dev/maven-snapshots/"){

    }
}

dependencies {
    implementation(project(":dclink-api"))
    implementation(libs.adventure.api)
    implementation(libs.adventure.minimessage)
    implementation(libs.cloud.core)
    implementation(libs.cloud.brigadier)
    compileOnly(libs.floodgate.api)
    implementation(libs.configurate.hocon)
    implementation(libs.jda) {
        exclude(module= "opus-java")
    }
    implementation(libs.sqlite)
}