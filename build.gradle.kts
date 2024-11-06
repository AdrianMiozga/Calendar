import edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask
import java.net.URI

plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("edu.sc.seis.launch4j") version "3.0.6"
    id("org.graalvm.buildtools.native") version "0.10.3"
}

val calendarMainClass = "org.wentura.calendar.Main"
val deployPath = "C:/Calendar"
group = "org.wentura.calendar"
version = "0.1.0"

repositories {
    mavenCentral()
    maven { url = URI("https://jitpack.io") }
}

dependencies {
    // Networking
    val retrofit = "2.11.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.12")

    // Dark Mode Detector
    implementation("com.github.Dansoftowner:jSystemThemeDetector:3.9.1")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.11.0-M2"))
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to calendarMainClass
        )
    }
}

application {
    mainClass = calendarMainClass
}

tasks.test {
    useJUnitPlatform()
}

tasks.clean {
    doLast {
        File("access-token.ser").delete()
        File("log").delete()
    }
}

tasks.shadowJar {
    archiveFileName = project.name.lowercase() + ".jar"
}

tasks.withType<DefaultLaunch4jTask> {
    mainClassName.set(calendarMainClass)
    outfile = project.name.lowercase() + ".exe"
    headerType = "console"
}

tasks.register<Copy>("deploy") {
    dependsOn("createExe")
    from("build/launch4j")
    into(deployPath)
}

graalvmNative {
    binaries {
        named("main") {
            mainClass.set(calendarMainClass)
            buildArgs.addAll("-H:+AddAllCharsets", "-O3", "--enable-url-protocols=http")
        }
    }

    agent {
        metadataCopy {
            mergeWithExisting.set(true)
        }
    }
}

tasks.register<Copy>("nativeDeploy") {
    dependsOn("nativeCompile")

    from("build/native/nativeCompile") {
        exclude("*.args")
    }

    into(deployPath)
}
