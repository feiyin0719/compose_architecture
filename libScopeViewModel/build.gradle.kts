import com.iffly.compose.buildsrc.Libs
import com.iffly.compose.buildsrc.Versions
plugins {
    id ("com.android.library")
    id ("org.jetbrains.kotlin.android")
}

android {

    compileSdk = Versions.targetVersion
    buildToolsVersion = Versions.buildToolVersion

    defaultConfig {

        minSdk = Versions.minVersion
        targetSdk = Versions.targetVersion

        testInstrumentationRunner =  "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled =  false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

dependencies {

    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.Compose.ui)
    implementation(Libs.AndroidX.Lifecycle.viewModelCompose)
    implementation(Libs.AndroidX.Navigation.navigationCompose)
    testImplementation(Libs.JUnit.junit)
}