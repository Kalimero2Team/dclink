plugins {
    java
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    repositories {
        maven {
            name = "byquantonRepo"
            url = if(version.toString().endsWith("-SNAPSHOT")) {
                uri("https://repo.byquanton.eu/snapshots")
            } else {
                uri("https://repo.byquanton.eu/releases")
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