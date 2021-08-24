plugins {
    id("com.android.application") 
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    kotlin("kapt")
}
val compose_version = "1.0.0"
android {

    compileSdk = 31
    buildToolsVersion = "31.0.0"

    defaultConfig {

        minSdk =  21
        targetSdk =  31

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
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = compose_version
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.6.0")
    implementation( "androidx.compose.ui:ui:$compose_version")
    implementation( "androidx.compose.material:material:$compose_version")
    implementation( "androidx.compose.ui:ui-tooling-preview:$compose_version")
    implementation( "androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation( "androidx.activity:activity-compose:1.3.1")
    implementation( "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")
    implementation( "androidx.navigation:navigation-compose:2.4.0-alpha05")
    implementation( "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1")
    implementation( "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation( "androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation( "androidx.compose.runtime:runtime-livedata:$compose_version")
    implementation( "androidx.activity:activity-ktx:1.3.1")
    implementation( project( ":libredux"))
    implementation( project( ":libScopeViewModel"))
    implementation( project( ":reduxannotation"))
    kapt(project(":redux_annotation_ksp"))
    implementation( "androidx.appcompat:appcompat:1.3.1")
    implementation( "com.google.android.material:material:1.4.0")
    testImplementation( "junit:junit:4.+")
    androidTestImplementation( "androidx.test.ext:junit:1.1.3")
    androidTestImplementation( "androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation( "androidx.compose.ui:ui-test-junit4:$compose_version")
    debugImplementation( "androidx.compose.ui:ui-tooling:$compose_version")
}