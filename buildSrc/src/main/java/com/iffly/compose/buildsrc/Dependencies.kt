package com.iffly.compose.buildsrc

object Versions {
    const val ktlint = "0.41.0"
    const val appGradleVersion = "7.1.0"
    const val targetVersion = 31
    const val minVersion = 21
    const val buildToolVersion = "31.0.0"
    const val jvmTarget = "1.8"
}

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.1"

    object Retrofit {
        const val version = "2.9.0"
        const val runtime = "com.squareup.retrofit2:retrofit:$version"
        const val gson = "com.squareup.retrofit2:converter-gson:$version"
    }

    object Accompanist {
        const val version = "0.15.0"
        const val insets = "com.google.accompanist:accompanist-insets:$version"
        const val swipeRefresh = "com.google.accompanist:accompanist-swiperefresh:$version"
        const val systemuicontroller =
            "com.google.accompanist:accompanist-systemuicontroller:$version"
    }

    object Kotlin {
        const val version = "1.5.10"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
    }

    object Coroutines {
        private const val version = "1.5.1"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.6.0"

        object Compose {
            const val snapshot = ""
            const val version = "1.0.1"

            const val foundation = "androidx.compose.foundation:foundation:$version"
            const val layout = "androidx.compose.foundation:foundation-layout:$version"
            const val ui = "androidx.compose.ui:ui:$version"
            const val uiUtil = "androidx.compose.ui:ui-util:$version"
            const val uiToolPreview = "androidx.compose.ui:ui-tooling-preview:$version"
            const val runtime = "androidx.compose.runtime:runtime:$version"
            const val material = "androidx.compose.material:material:$version"
            const val animation = "androidx.compose.animation:animation:$version"
            const val tooling = "androidx.compose.ui:ui-tooling:$version"
            const val iconsExtended = "androidx.compose.material:material-icons-extended:$version"
            const val uiTest = "androidx.compose.ui:ui-test-junit4:$version"
            const val liveData = "androidx.compose.runtime:runtime-livedata:$version"
        }

        object Activity {
            const val activityCompose = "androidx.activity:activity-compose:1.3.1"
            const val activityKtx = "androidx.activity:activity-ktx:1.3.1"
        }

        object Lifecycle {
            const val viewModelCompose =
                "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07"
            const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:2.3.1"
        }

        object LiveData {
            const val version = "2.3.1"
            const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
        }

        object Navigation {
            const val navigationCompose = "androidx.navigation:navigation-compose:2.4.0-alpha07"
        }

        object ConstraintLayout {
            const val constraintLayoutCompose =
                "androidx.constraintlayout:constraintlayout-compose:1.0.0-beta01"
        }

        object Test {
            private const val version = "1.3.0"
            const val runner = "androidx.test:runner:$version"
            const val rules = "androidx.test:rules:$version"

            object Ext {
                private const val version = "1.1.3"
                const val junit = "androidx.test.ext:junit-ktx:$version"
            }

            const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
        }

        object Appcompat {
            const val version = "1.3.1"
            const val appCompat = "androidx.appcompat:appcompat:$version"
        }

        object Hilt {
            const val version = "2.38.1"
            const val hiltAndroid = "com.google.dagger:hilt-android:$version"
            const val hiltAndroidCompile = "com.google.dagger:hilt-android-compiler:$version"
            const val hiltViewModel = "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03"
            const val hiltCompile = "androidx.hilt:hilt-compiler:1.0.0"
        }
    }

    object JUnit {
        private const val version = "4.13"
        const val junit = "junit:junit:$version"
    }

    object Coil {
        const val coilCompose = "io.coil-kt:coil-compose:1.3.0"
    }

    object Material {
        const val version = "1.4.0"
        const val material = "com.google.android.material:material:$version"
    }

}
