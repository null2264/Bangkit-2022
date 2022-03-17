import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.konan.properties.hasProperty

val localProperties = gradleLocalProperties(rootDir)

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.parcelize")
    id("com.apollographql.apollo3").version("3.1.0")
    id("kotlin-android")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "io.github.null2264.githubuser"
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Default value is set to \"\" because defaultValue's type is not handled for some reason
        buildConfigField("String", "clientId", localProperties.getProperty("clientId", "\"\""))
        buildConfigField("String", "clientSecret", localProperties.getProperty("clientSecret", "\"\""))

        resValue("string", "redirect_scheme",
            localProperties.getProperty("redirectScheme", "null2264.githubuser"))
        buildConfigField("String", "redirectScheme",
            localProperties.getProperty("redirectScheme", "\"null2264.githubuser\""))
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // AndroidX
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.core:core-splashscreen:1.0.0-beta01")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.fragment:fragment-ktx:1.4.1")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")
    implementation("androidx.browser:browser:1.4.0")

    // Android Material
    implementation("com.google.android.material:material:1.5.0")

    // apollo (for GitHub's GraphQL API)
    implementation("com.apollographql.apollo3:apollo-runtime:3.1.0")

    // okhttp
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // glide
    implementation("com.github.bumptech.glide:glide:4.13.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.1")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // view binding QOL
    implementation("com.github.kirich1409:viewbindingpropertydelegate:1.5.6")

    // test unit
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

apollo {
    packageName.set("io.github.null2264.githubuser")
}