plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.mikepenz.aboutlibraries)
}

android {
    namespace = "net.lag129.ferret"
    compileSdk = 36

    defaultConfig {
        applicationId = "net.lag129.ferret"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    //noinspection WrongGradleMethod
    composeCompiler {
        stabilityConfigurationFiles.addAll(
            project.layout.projectDirectory.file("stability_config.conf"),
        )
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    //noinspection WrongGradleMethod
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // AboutLibraries
    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose.core)
    implementation(libs.aboutlibraries.compose.m3)

    // androidx.datastore:datastore-preferences
    implementation(libs.androidx.datastore.preferences)

    // androidx.navigation3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)

    // Coil3
    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)
    implementation(libs.coil.gif)

    // DataStore Crypto
    implementation(libs.datastore.crypto.preferences)

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.navigation)

    // Kotest
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.extensions.koin)

    // kotlinx.collections.immutable
    implementation(libs.kotlinx.collections.immutable)

    // kotlinx.datetime
    implementation(libs.kotlinx.datetime)

    // kotlinx.serialization
    implementation(libs.kotlinx.serialization.json)

    // Ksoup
    implementation(libs.ksoup.html)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // MLKit
    implementation(libs.translate)
    implementation(libs.kotlinx.coroutines.play.services)

    // Mockk
    testImplementation(libs.mockk)

    // Napier
    implementation(libs.napier)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Compose Lint
    lintChecks(libs.compose.lint.checks)

    // LeakCanary
    debugImplementation(libs.leakcanary.android)
}