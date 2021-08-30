// Top-level build file where you can add configuration options common to all sub-projects/modules.


buildscript {

    repositories {
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.5.10"))
        classpath("com.android.tools.build:gradle:7.0.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
        classpath( "com.google.dagger:hilt-android-gradle-plugin:2.38.1")
    }
}


subprojects {
    repositories {
        jcenter()
        google()
        mavenCentral()


    }

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            kotlinOptions {
                // Treat all Kotlin warnings as errors (disabled by default)
                allWarningsAsErrors = false

                freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
                // Enable experimental coroutines APIs, including Flow
                freeCompilerArgs += "-Xopt-in=kotlin.Experimental"

                // Set JVM target to 1.8
                jvmTarget = "1.8"
            }
        }
    }

}