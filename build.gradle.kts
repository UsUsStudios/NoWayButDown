plugins {
    id("java")
    id("application")
}

group = "com.ususstudios.noway"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.ususstudios.noway.main.Game")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.json:json:20250107")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.24.0")
    implementation("org.apache.logging.log4j:log4j-core:2.24.0")
    annotationProcessor("org.apache.logging.log4j:log4j-core:2.24.1")
}

tasks.test {
    useJUnitPlatform()
}