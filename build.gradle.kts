// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.devtools.ksp) version "1.9.23-1.0.20" apply false
    alias(libs.plugins.google.hilt.android) version "2.52" apply false
    alias(libs.plugins.androidx.room) apply false
}