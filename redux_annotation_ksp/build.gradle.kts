val kspVersion:String = "1.5.10-1.0.0-beta01"

plugins {
    id("java-library")
    id("kotlin")
    id("com.google.devtools.ksp")
    kotlin("jvm")
}



java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies{
    implementation(kotlin("stdlib"))
    implementation(project(":reduxannotation"))
    compileOnly("com.google.devtools.ksp:symbol-processing-api:$kspVersion")

    implementation("com.google.auto.service:auto-service-annotations:1.0")
    ksp("dev.zacsweers.autoservice:auto-service-ksp:0.5.2")
    implementation("com.squareup:kotlinpoet:1.8.0")
}