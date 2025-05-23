

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("maven-publish")
}

android {
    namespace = "com.shuzhi.opencv"
    compileSdk = 34

    defaultConfig {
       // applicationId = "com.shuzhi.opencv"
        minSdk = 25
        targetSdk = 34
        //versionCode = 1
        //versionName = "1.0"

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
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    aaptOptions {
        noCompress.add("tflite")
        noCompress.add("lite")
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

dependencies {
    //提供多种滤镜
    api("jp.co.cyberagent.android:gpuimage:2.1.0")
    //图片加载核心框架
    implementation ("io.coil-kt:coil-compose:2.6.0")
    // Hilt 核心库
    implementation("com.google.dagger:hilt-android:2.55")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.1")
    implementation("androidx.compose.animation:animation-core-lint:1.8.0")
    kapt("com.google.dagger:hilt-android-compiler:2.55")

    // Hilt + Compose 支持
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    //compose Navigation
    implementation("androidx.navigation:navigation-compose:2.8.8")
    implementation ("com.google.accompanist:accompanist-navigation-animation:0.31.1-alpha")
    //viewModel for compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    // Preferences DataStore（适合简单键值对）
    implementation ("androidx.datastore:datastore-preferences:1.1.3")
    //图片轮廓识别
    implementation("com.github.pqpo:SmartCropper:v2.1.3")
    //Google mlkit 文档扫描器
    implementation ("com.google.android.gms:play-services-mlkit-document-scanner:16.0.0-beta1")
    //opencv
    //implementation(project(":opencv410"))
    // CameraX
    val camerax_version = "1.4.1"
    implementation ("androidx.camera:camera-core:${camerax_version}")
    implementation ("androidx.camera:camera-view:${camerax_version}")
    implementation ("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation ("androidx.camera:camera-camera2:${camerax_version}")
    implementation ("androidx.camera:camera-extensions:${camerax_version}")
    // Kotlin Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    // ML Kit ocr 文字识别
    implementation ("com.google.mlkit:text-recognition-chinese:16.0.1")
    //itex 压缩pdf
    implementation("com.itextpdf:itext7-core:9.2.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    api(platform("androidx.compose:compose-bom:2025.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    implementation("cn.leancloud:storage-android:8.2.28")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
kapt {
    correctErrorTypes = true // 必须配置
}

group = "com.shuzhi.opencv"

publishing{
    publications {
        // register和create都差不多
        //定义sdk
        register<MavenPublication>("lib") {
            // 配置信息，使用: classpath("groupId:artifactId:version") (不能有空格)
            groupId = "com.shuzhi.opencv"
            artifactId = "document"
            version = "v0.0.2"
            // 这条要加上，不然不会包含代码文件
            afterEvaluate {
                from(components["release"])
            }
        }
    }
    //sdk所在仓库
    repositories {
        // 远程仓库
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/2631140624/my-lens")
            credentials {

                username = rootProject.extra["githubUser"].toString()
                password = rootProject.extra["githubPassword"].toString()
            }
        }
    }
}


