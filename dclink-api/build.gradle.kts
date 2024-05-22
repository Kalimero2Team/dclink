plugins {
    java
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


publishing {
    repositories {
        maven {
            name = "kalimero2Repo"
            url = if(version.toString().endsWith("-SNAPSHOT")) {
                uri("https://repo.kalimero2.com/snapshots")
            } else {
                uri("https://repo.kalimero2.com/releases")
            }
            credentials {
                username = System.getenv("REPO_USERNAME")
                password = System.getenv("REPO_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
