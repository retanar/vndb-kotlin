plugins {
    kotlin("jvm") version "1.5.21"
    `java-library`
}

version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    val ktorVersion = "1.6.1"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-network-tls:$ktorVersion")
    implementation("com.google.code.gson:gson:2.8.7")

    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveFileName.set("${rootProject.name}-${archiveVersion.get()}.jar")
}

tasks.register<Jar>("fatJar") {
    archiveFileName.set("${rootProject.name}-fat-${archiveVersion.get()}.jar")

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}