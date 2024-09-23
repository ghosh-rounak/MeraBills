plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.devtoolsKsp) // added for ksp plugin
    alias(libs.plugins.kotlinKapt) //added Kotlin kapt plugin
    alias(libs.plugins.androidNavigation) //safe args plugin for navigation component
    alias(libs.plugins.daggerHilt) //added for HILT
}

android {
    namespace = "com.rounak.merabills"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rounak.merabills"
        minSdk = 21
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

    //enable data binding
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)

    // mdc
    implementation(libs.material)

    // constraint layout
    implementation(libs.androidx.constraintlayout)

    //ViewModel And LiveData Related Dependencies------------------
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)
    // alternately - if using Java8, use the following instead of lifecycle-compiler
    implementation(libs.androidx.lifecycle.common.java8)

    //coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)


    //Room related dependencies--------------
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)


    //Navigation Component Dependencies-------------------------------
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //ssp
    implementation(libs.ssp.android)

    //sdp
    implementation(libs.sdp.android)

    //HILT
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    //recycler view
    implementation(libs.androidx.recyclerview)
}

//added for HILT
kapt {
    correctErrorTypes = true
}

//added for HILT
hilt {
    enableAggregatingTask = true
}