import com.iffly.compose.buildsrc.Libs
import com.iffly.compose.buildsrc.Versions
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}
android {

    compileSdk = Versions.targetVersion
    buildToolsVersion = Versions.buildToolVersion

    defaultConfig {

        minSdk = Versions.minVersion
        targetSdk = Versions.targetVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            kapt {

            }
            sourceSets {
                getByName("main") {
                    java.srcDir(File("build/generated/ksp/release/kotlin"))
                }
            }
        }

        debug {
            kapt {}
            sourceSets {
                getByName("main") {
                    java.srcDir(File("build/generated/ksp/debug/kotlin"))
                }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = Versions.jvmTarget
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Libs.AndroidX.Compose.version
    }
}
ksp {

}
dependencies {

    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.Compose.ui)
    implementation(Libs.AndroidX.Compose.material)
    implementation(Libs.AndroidX.Compose.uiToolPreview)
    implementation(Libs.AndroidX.Lifecycle.runtimeKtx)
    implementation(Libs.AndroidX.Activity.activityCompose)
    implementation(Libs.AndroidX.Lifecycle.viewModelCompose)
    implementation(Libs.AndroidX.Navigation.navigationCompose)
    implementation(Libs.Coroutines.android)
    implementation(Libs.Coroutines.core)
    implementation(Libs.AndroidX.LiveData.liveDataKtx)
    implementation(Libs.AndroidX.Compose.liveData)
    implementation(Libs.AndroidX.Activity.activityKtx)
    implementation(project(":libredux"))
    implementation(project(":libScopeViewModel"))
    implementation(project(":reduxannotation"))
    implementation(Libs.AndroidX.Hilt.hiltAndroid)
    kapt(Libs.AndroidX.Hilt.hiltAndroidCompile)
    implementation(Libs.AndroidX.Hilt.hiltViewModel)
    // When using Kotlin.
    kapt(Libs.AndroidX.Hilt.hiltCompile)
    ksp(project(":redux_annotation_ksp"))
    implementation(Libs.AndroidX.Appcompat.appCompat)
    implementation(Libs.Material.material)
    testImplementation(Libs.JUnit.junit)
    androidTestImplementation(Libs.AndroidX.Test.Ext.junit)
    androidTestImplementation(Libs.AndroidX.Test.espressoCore)
    androidTestImplementation(Libs.AndroidX.Compose.uiTest)
    debugImplementation(Libs.AndroidX.Compose.tooling)
}