pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        id("com.android.application") version "7.1.0"
        id("com.android.library") version "7.1.0"
        id("org.jetbrains.kotlin.android") version "1.5.10"
        id("com.google.devtools.ksp") version "1.5.10-1.0.0-beta01" apply false
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "compose_redux"
include(":app")
include(":libredux")
include(":libScopeViewModel")
include(":reduxannotation")
include(":redux_annotation_ksp")
