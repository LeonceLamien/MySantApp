plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.my_health_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.my_health_app"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.databinding.runtime)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation("com.mapbox.mapboxsdk:mapbox-android-sdk:9.2.1"){
        exclude (group= "com.android.support")
    }
    implementation ("com.mapbox.mapboxsdk:mapbox-android-plugin-locationlayer:0.11.0"){
        exclude (group= "com.android.support")
    }
    implementation ("com.mapbox.mapboxsdk:mapbox-android-plugin-annotation-v9:0.9.0"){
        exclude (group= "com.android.support")
    }
    implementation ("com.github.highcharts:highcharts-android:11.4.1")



    implementation(libs.leanback)
    implementation(libs.glide)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    implementation ("androidx.appcompat:appcompat:1.3.0")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}