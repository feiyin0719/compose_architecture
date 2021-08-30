plugins {
    id ("com.android.library")
    id ("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
//    id "maven-publish"
}

val compose_version = "1.0.0"
val hiltVersion = "2.38.1"
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

    implementation( "androidx.core:core-ktx:1.6.0")
    implementation ("androidx.compose.ui:ui:1.0.0")
    implementation("androidx.compose.material:material:1.0.0")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.0.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation ("androidx.activity:activity-compose:1.3.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")
    implementation( "androidx.navigation:navigation-compose:2.4.0-alpha07")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.0.0")
    implementation ("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation ("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    // When using Kotlin.
    kapt ("androidx.hilt:hilt-compiler:1.0.0")
    testImplementation ("junit:junit:4.+")
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
}


//ext {
//    // 从Github上clone下来的项目的本地地址
//    GITHUB_REPO_PATH = "../"
//    PUBLISH_GROUP_ID = "com.iffly"
//    PUBLISH_ARTIFACT_ID = "redux"
//    PUBLISH_VERSION = "0.0.3"
//}
//// 源代码一起打包
////生成源码jar包task，type表示继承Jar打包任务。
//task sourcesJar(type: Jar) {
//    from android.sourceSets.main.java.srcDirs
//    archiveClassifier.set("sources")
//}
//artifacts {
//    archives sourcesJar
//}
//afterEvaluate {
//    publishing {
//        publications {
//            mavenJava(MavenPublication) {
//                from components.release
//                artifact sourcesJar
//
//                groupId project.PUBLISH_GROUP_ID
//                artifactId project.PUBLISH_ARTIFACT_ID
//                version project.PUBLISH_VERSION
//            }
//        }
//        repositories {
//            maven {
//                def deployPath = file(project.GITHUB_REPO_PATH)
//                url "file://${deployPath.absolutePath}"
//            }
//        }
//    }
//}