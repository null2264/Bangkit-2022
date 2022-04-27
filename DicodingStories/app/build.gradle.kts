import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

val localProperties = gradleLocalProperties(rootDir)

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs")
    id("de.mannodermaus.android-junit5")
}

@Suppress("UnstableApiUsage")
android {
    compileSdk = 31

    defaultConfig {
        applicationId = "io.github.null2264.dicodingstories"
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "io.github.null2264.dicodingstories.CustomTestRunner"

        resValue("string", "gmaps_api_key",
            localProperties.getProperty("gmapsApiKey", ""))
        buildConfigField("String", "gmapsApiKey",
            localProperties.getProperty("gmapsApiKey", "\"\""))
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    compileOptions {
        // JDK 11 is Android's new recommended JDK
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // --- API/Library from Google ---
    implementation("com.google.android.material:material:1.5.0")

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")

    implementation("androidx.fragment:fragment-ktx:1.4.1")

    implementation("androidx.navigation:navigation-fragment-ktx:2.4.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.2")

    implementation("androidx.preference:preference-ktx:1.2.0")

    implementation("androidx.core:core-splashscreen:1.0.0-beta02")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")

    implementation("androidx.camera:camera-camera2:1.1.0-beta03")
    implementation("androidx.camera:camera-lifecycle:1.1.0-beta03")
    implementation("androidx.camera:camera-view:1.1.0-beta03")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("androidx.paging:paging-runtime-ktx:3.1.1")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // hilt+dagger
    implementation("com.google.dagger:hilt-android:2.41")
    kapt("com.google.dagger:hilt-compiler:2.41")

    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.gms:play-services-location:19.0.1")

    implementation("androidx.test.espresso:espresso-idling-resource:3.4.0")

    // --- third parties ---
    // view binding QOL
    implementation("com.github.kirich1409:viewbindingpropertydelegate:1.5.6")

    // okhttp
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    implementation("com.github.bumptech.glide:glide:4.13.1")
    kapt("com.github.bumptech.glide:compiler:4.13.1")

    // --- Testing stuff ---

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("io.mockk:mockk:1.12.3")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")

    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.41")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.41")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0")

    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.9.3")
    androidTestImplementation("com.squareup.okhttp3:okhttp-tls:4.9.3")

    debugImplementation("androidx.fragment:fragment-testing:1.4.1")
    debugImplementation("androidx.navigation:navigation-testing:2.4.2")
}