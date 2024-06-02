plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("plugin.serialization") version (libs.versions.kotlin)
    id("androidx.navigation.safeargs")
}

android {
    namespace = "ru.hse.miem.yandexsmarthomeapi"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.hse.miem.yandexsmarthomeapi"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.json.v170rc)
    implementation(libs.androidx.multidex)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android.v181beta)

    //SLF4J
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.simple)
    implementation(libs.kotlin.logging)

    //Codified enums
    implementation(libs.enums)
    implementation(libs.enums.serializer)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.json)
    implementation(libs.ktor.client.serialization)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.auth)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.mock)

    // Testing
    testImplementation(libs.ktor.client.mock.jvm)
    testImplementation(libs.mockk)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.kotlinx.coroutines.test)

    //Ui
    implementation(libs.andcolorpicker)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)
    implementation(libs.draggableview)
    implementation(libs.snackify)
    implementation(libs.androidcolorx)
    implementation(libs.androidx.swiperefreshlayout)


    //Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.dynamic.features.fragment)
}