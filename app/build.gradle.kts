plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.toutiaodemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.toutiaodemo"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

// build.gradle.kts (Module: app)

dependencies {
    // AndroidX Core & Lifecycle (使用最新的稳定版本)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // 🔥 核心修改：使用 Compose BOM 统一版本
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))

    // 以下所有 Compose 依赖都不再指定版本号，由 BOM 统一管理
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // M3 兼容的 Icons Extended
    implementation("androidx.compose.material:material-icons-extended")

    // 下拉刷新替代方案：Accompanist SwipeRefresh (保持您使用的版本 0.34.0)
    // 注意：这个库需要单独指定版本
    implementation("com.google.accompanist:accompanist-swiperefresh:0.34.0")

    // 图片加载 Coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}