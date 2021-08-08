plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.20"
    `java-library`
}

version = "0.1.0"

repositories {
    mavenCentral()
}

val ktorVersion = "1.6.1"

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-network-tls:$ktorVersion")
    implementation("com.google.code.gson:gson:2.8.7")

    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

tasks.test {
    useJUnitPlatform()
}