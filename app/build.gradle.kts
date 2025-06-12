plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.androidmaster"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.androidmaster"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // libreria para animaciones
    implementation("com.airbnb.android:lottie:6.6.6")
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Google Maps SDK
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // (Opcional) Ubicación del dispositivo
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Firebase Authentication (para login de usuario)
    implementation("com.google.firebase:firebase-auth")

   // Firebase Realtime Database (para enviar y leer ubicación del ESP32)
    implementation("com.google.firebase:firebase-database")

    implementation("androidx.cardview:cardview:1.0.0")




}