version = "0.0.1"

group = "com.flizpay2"

plugins {
    id("com.android.library") version "8.7.3"
    id("org.jetbrains.kotlin.android") version "1.8.0"
}

android {
    namespace = "com.flizpay2"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testOptions {
            targetSdk = 34
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = "17" }
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.gson.v2110)
    implementation (libs.okhttp)


    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.jupiter.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}
