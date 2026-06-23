import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}

val properties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.example.sedo"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.sedo"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        val apiKey = properties.getProperty("OPENWEATHER_API_KEY") ?: "\"\""
        buildConfigField("String", "OPENWEATHER_API_KEY", apiKey)

        val geminiKey = properties.getProperty("GEMINI_API_KEY") ?: "\"\""
        buildConfigField("String", "GEMINI_API_KEY", geminiKey)

        val ytKey = properties.getProperty("YOUTUBE_API_KEY") ?: "\"\""
        buildConfigField("String", "YOUTUBE_API_KEY", ytKey)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    androidResources {
        noCompress += "tflite"
    }
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src/main/assets")
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Coroutine (비동기 처리 - 20점)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Retrofit2 & Gson (API 통신 - 60점 달성용)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Glide (이미지 로딩 - 다운로드 매니저 20점)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:compiler:4.16.0")

    // Room DB (내 옷장 데이터 관리용)
    val roomVersion = "2.7.0"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion") // Coroutine 지원용
    ksp("androidx.room:room-compiler:$roomVersion")

    // Jetpack Navigation & ViewModel (Jetpack 30점)
    val navVersion = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // TensorFlow Lite 의존성 추가
    implementation("org.tensorflow:tensorflow-lite:2.13.0")

    // Google ML Kit OCR (한국어/영어 혼용 지원 최신 모듈)
    implementation("com.google.mlkit:text-recognition-korean:16.0.1")
    // ML Kit의 Task를 코루틴(await)으로 쉽게 쓰기 위한 라이브러리
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Google Gemini AI SDK
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // 뱃지 자동 줄바꿈을 위한 Flexbox
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // GPS 위치 정보 가져오기
    implementation("com.google.android.gms:play-services-location:21.2.0")
}
