plugins {
    `java-library`
}

allprojects{
    group = "com.kalimero2.team"
    version = "1.0.6-SNAPSHOT"
    description = "Discord Bot for linking Minecraft Accounts with Discord Accounts"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

subprojects{
    apply{
        plugin("java-library")
    }
}