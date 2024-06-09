import java.util.Properties

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }
}


// Top-level build file where you can add configuration options common to all sub-projects/modules.
ext {
    val mapkitApiKey: String by lazy { getMapkitApiKey() }
}
 fun getMapkitApiKey(): String {
    val properties = Properties()
    project.file("local.properties").inputStream().use { properties.load(it) }
    return properties.getProperty("MAPKIT_API_KEY", "")
}

plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("androidx.navigation.safeargs") version "2.5.0" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}

