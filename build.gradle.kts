
plugins {
    kotlin("jvm") version "1.8.0"
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
