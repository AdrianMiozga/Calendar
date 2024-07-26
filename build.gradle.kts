import edu.sc.seis.launch4j.tasks.DefaultLaunch4jTask

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("edu.sc.seis.launch4j") version "3.0.6"
}

val mainClass = "org.wentura.calendar.Main"
group = "org.wentura.calendar"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    val retrofit = "2.11.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit")

    testImplementation(platform("org.junit:junit-bom:5.11.0-M2"))
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to mainClass
        )
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.clean {
    doLast {
        File("AccessToken.ser").delete()
    }
}

tasks.withType<DefaultLaunch4jTask> {
    mainClassName.set(mainClass)
    headerType = "console"
}
