plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    // 1. 在 plugins 代码块的末尾添加 KSP 插件
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.moneytracker"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.moneytracker"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

ksp {
    arg("room.generateKotlin", "true")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // 2. 在 dependencies 代码块末尾添加 Room 的依赖
    val roomVersion = "2.7.0"
    implementation("androidx.room:room-runtime:$roomVersion")
    // Room 的 Kotlin 扩展和协程支持
    implementation("androidx.room:room-ktx:$roomVersion")
    // 使用 KSP 处理 Room 的注解并生成底层实现代码
    ksp("androidx.room:room-compiler:$roomVersion")

    // 👇 添加这两行：引入 Compose Material 图标库
    implementation("androidx.compose.material:material-icons-core")
    // extended 库包含了所有的 Material 官方图标（包括以后我们会用到的各种记账分类图标）
    implementation("androidx.compose.material:material-icons-extended")
}