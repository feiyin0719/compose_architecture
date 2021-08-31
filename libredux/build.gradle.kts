import com.iffly.compose.buildsrc.Libs
import com.iffly.compose.buildsrc.Versions

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
//    id "maven-publish"
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
    implementation(Libs.AndroidX.Lifecycle.runtimeKtx)
    implementation(Libs.AndroidX.Activity.activityCompose)
    implementation(Libs.AndroidX.Lifecycle.viewModelCompose)
    implementation(Libs.AndroidX.LiveData.liveDataKtx)
    implementation(Libs.AndroidX.Compose.liveData)
    implementation(Libs.AndroidX.Activity.activityKtx)
    implementation(Libs.AndroidX.Hilt.hiltAndroid)
    kapt(Libs.AndroidX.Hilt.hiltAndroidCompile)
    implementation(Libs.AndroidX.Hilt.hiltViewModel)
    // When using Kotlin.
    kapt(Libs.AndroidX.Hilt.hiltCompile)

    testImplementation(Libs.JUnit.junit)

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