plugins {
    id("java")
}

group = "me.kieran"
version = "1.0.1"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.helpch.at/releases/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.12.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}