plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.velocity.run)
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

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    runVelocity {
        velocityVersion("3.1.2-SNAPSHOT")
    }

    shadowJar {
        archiveClassifier.set("")

        fun reloc(pkg: String) = relocate(pkg, "com.kalimero2.team.dclink.libs.$pkg")
        reloc("cloud.commandframework")
        reloc("io.leangen")
        reloc("net.dv8tion")
        reloc("org.xerial")
    }
}