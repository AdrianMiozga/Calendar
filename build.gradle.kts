plugins {
    id("java")
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
