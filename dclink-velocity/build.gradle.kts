plugins {
    @Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

dependencies {
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)
    implementation(libs.adventure.minimessage)
    implementation(libs.cloud.velocity)
    implementation(project(":dclink-api"))
    implementation(project(":dclink-core"))
}

tasks{
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar{
        fun reloc(pkg: String) = relocate(pkg, "com.kalimero2.team.dclink.libs.cloud.dependency.$pkg")
        reloc("cloud.commandframework")
        reloc("io.leangen")
        reloc("net.dv8tion")
        reloc("org.xerial")
    }
}