version = "0.1.0"
group = "com.flizpay2"

plugins {
    id("com.android.library") version "8.7.3"
    id("org.jetbrains.kotlin.android") version "1.8.0"
    id("maven-publish")
}

android {
    namespace = "com.flizpay2"
    compileSdk = 34

    defaultConfig.apply {
        minSdk = 21
        versionCode = 1
        versionName = project.version.toString()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = "17" }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.github.flizpay"
                artifactId = "flizpay-sdk"
                version = "0.1.0"

                from(components["release"])
            }
        }
    }
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
    implementation(libs.okhttp)
    implementation(libs.security.crypto)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.jupiter.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

task<Delete>("clearJar") {
    delete("build/libs/FlizpaySDK.jar")
}

task<Copy>("makeJar") {
    from("build/intermediates/aar_main_jar/release/syncReleaseLibJars/", "build/intermediates/full_jar/debug/createFullJarDebug")
    into("build/libs/")
    include("classes.jar")
    rename { _: String -> "FlizpaySDK.jar" }
    dependsOn("clearJar", "build")
}
