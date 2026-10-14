
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.retroboy.emulator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.retroboy.emulator"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
