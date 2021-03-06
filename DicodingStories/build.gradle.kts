// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.41")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.2")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.8.2.0")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}