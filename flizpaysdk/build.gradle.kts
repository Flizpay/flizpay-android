version = "0.1.0"
group = "com.flizpay2"

plugins {
    id("com.android.library") version "8.7.3"
    id("org.jetbrains.kotlin.android") version "1.8.0"
    id("maven-publish")
    id("jacoco")
}

android {
    namespace = "com.flizpay2"
    compileSdk = 34

    defaultConfig.apply {
        minSdk = 21
        versionCode = 1
        versionName = project.version.toString()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }

        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
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

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    packaging {
        resources {
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE.txt")
            excludes.add("META-INF/DEPENDENCIES")
            excludes.add("META-INF/LICENSE-notice.md")
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
    implementation(libs.ui.test.android)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.jupiter.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.robolectric)
    testImplementation(libs.core.v150)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.core)
    testImplementation(libs.byte.buddy)
    
    // AndroidX Test core dependencies
    androidTestImplementation(libs.junit)  // JUnit support
    androidTestImplementation(libs.espresso.core) // UI testing
    androidTestImplementation(libs.runner)  // Test runner
    androidTestImplementation(libs.rules)   // Test rules
    androidTestImplementation(libs.junit.ktx)
    androidTestImplementation(libs.uiautomator)

    // MockK for Android instrumented tests
    androidTestImplementation(libs.mockk.android)

    // Coroutine testing support
    androidTestImplementation(libs.kotlinx.coroutines.test.v164)
    androidTestImplementation(libs.jupiter.junit.jupiter)
}

jacoco {
    toolVersion = "0.8.7"  // Use the appropriate Jacoco version
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.getByName("testDebugUnitTest"))
    dependsOn(tasks.getByName("connectedDebugAndroidTest"))

    reports {
        xml.apply {
            isEnabled = true
            outputLocation.set(file("build/reports/jacoco/jacoco.xml"))
        }
        html.apply {
            isEnabled = true
            outputLocation.set(file("build/reports/jacoco/html"))
        }
    }

    val buildDir = "$projectDir/build"

    // Unit test coverage report
    sourceDirectories.setFrom(files("${projectDir}/src/main/kotlin"))
    classDirectories.setFrom(files("${buildDir}/intermediates/javac/debug/classes"))
    executionData.setFrom(files("${buildDir}/jacoco/testDebugUnitTest.exec"))
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

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("robolectric.logging", "stdout")
}
