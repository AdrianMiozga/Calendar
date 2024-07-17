plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.example"
version = "0.1.0"

repositories {
    mavenCentral()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "org.example.Main"
        )
    }
}

dependencies {
    val retrofit = "2.11.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit")
}

tasks.clean {
    doLast {
        File("AccessToken.ser").delete()
    }
}
