import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.konan.properties.hasProperty

val localProperties = gradleLocalProperties(rootDir)

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.parcelize")
    kotlin("kapt")
    id("com.apollographql.apollo3").version("3.1.0")
    id("dagger.hilt.android.plugin")
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
        dataBinding = true
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
    // core and important stuff
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.core:core-splashscreen:1.0.0-beta01")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    // fragment
    implementation("androidx.fragment:fragment-ktx:1.5.0-alpha03")
    // activity
    implementation("androidx.activity:activity-ktx:1.4.0")
    // lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")
    // browser (custom chrome tab)
    implementation("androidx.browser:browser:1.4.0")
    // legacy support (tbh idk what does this do)
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    // room stuff
    implementation("androidx.room:room-runtime:2.4.2")
    implementation("androidx.room:room-ktx:2.4.2")
    kapt("androidx.room:room-compiler:2.4.2")
    // navigation stuff
    implementation("androidx.navigation:navigation-runtime-ktx:2.4.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.1")
    // datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // preferencesDataStore
    implementation("androidx.preference:preference:1.2.0")
    // hilt
    implementation("com.google.dagger:hilt-android:2.41")
    kapt("com.google.dagger:hilt-android-compiler:2.41")

    // Android Material
    implementation("com.google.android.material:material:1.5.0")

    // apollo (for GitHub's GraphQL API)
    implementation("com.apollographql.apollo3:apollo-runtime:3.1.0")

    // okhttp
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // glide
    implementation("com.github.bumptech.glide:glide:4.13.1")
    kapt("com.github.bumptech.glide:compiler:4.13.1")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // view binding QOL
    implementation("com.github.kirich1409:viewbindingpropertydelegate:1.5.6")

    // --- [start] Testing stuff
    // test unit
    testImplementation("junit:junit:4.13.2")

    // ui test
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    // --- [end]
}

kapt {
    correctErrorTypes = true
}

apollo {
    packageName.set("io.github.null2264.githubuser")
    codegenModels.set("responseBased")
}