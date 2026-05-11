plugins {
    java
    application
}

group = "ru.lottery"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

application {
    mainClass = "ru.lottery.Main"
}

repositories {
    mavenCentral()
}

dependencies {
    // JDBC Driver
    implementation("org.postgresql:postgresql:42.7.3")

    // Connection Pool
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.slf4j:slf4j-api:2.0.13")

    // JSON processing
    implementation("com.google.code.gson:gson:2.10.1")

    // Dotenv for configuration
    implementation("io.github.cdimascio:java-dotenv:5.2.2")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.mockito:mockito-core:5.12.0")
}

tasks.test {
    useJUnitPlatform()
}

// Правильная конфигурация для создания fat JAR
tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }

    // Собираем все зависимости в один JAR
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    }) {
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Альтернативный способ создания fat JAR через task
tasks.register<Jar>("fatJar") {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
    archiveClassifier.set("all")
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    }) {
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Чтобы fatJar выполнялся при сборке
tasks.build {
    dependsOn(tasks.named("fatJar"))
}