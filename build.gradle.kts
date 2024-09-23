// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false // must
    alias(libs.plugins.androidLibrary) apply false // must
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false // must
    alias(libs.plugins.devtoolsKsp) apply false // ksp
    alias(libs.plugins.androidNavigation) apply false // navigation
    alias(libs.plugins.daggerHilt) apply false // hilt
    alias(libs.plugins.kotlinKapt) apply false // kapt
}

tasks.create<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}