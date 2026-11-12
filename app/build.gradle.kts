plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" // Voeg deze regel toe
}

android {
    namespace = "com.borsosistvan.hitjamtv"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.borsosistvan.hitjamtv"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    // VERWIJDER het complete oude 'composeOptions { kotlinCompilerExtensionVersion = ... }' blok!
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    
    // Jetpack Compose alapok és Material 3 dizájn
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    
    // Officiële stabiele Jetpack Compose TV componenten van Google
    //implementation("androidx.tv:tv-foundation:1.0.0")
    //implementation("androidx.tv:tv-material:1.0.0")

    // ExoPlayer voor .m3u8 streaming
    implementation("androidx.media3:media3-exoplayer:1.4.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.4.0")
    implementation("androidx.media3:media3-ui:1.4.0")

    // Google GSON voor het online inladen en verwerken van de stations.json list
    implementation("com.google.code.gson:gson:2.11.0")
}

