plugins {
    id("java-library")
    id("kotlin")
    id("com.google.devtools.ksp") version "1.5.10-1.0.0-beta01"
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies{
    implementation(kotlin("stdlib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.5.10-1.0.0-beta01")
}