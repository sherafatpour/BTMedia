import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("maven-publish")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.compilations.getByName("main") {
            val nskeyvalueobserving by cinterops.creating
        }
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "shared"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(compose.runtime)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.guava)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.workmanager)

            implementation(libs.androidx.media3.common)
            implementation(libs.androidx.media3.session)
            implementation(libs.androidx.media3.exoplayer)

            implementation(libs.androidx.work.runtime.ktx)
            implementation(libs.koin.android)
            implementation(compose.ui)
        }

        iosMain.dependencies {

        }

        all {
            languageSettings.optIn("kotlin.uuid.ExperimentalUuidApi")
        }
    }
}

android {
    namespace = "io.github.moonggae.kmedia"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["kotlin"])
            groupId = "com.github.sherafatpour"
            artifactId = "BTMedia"
            version = "1.0.0"
        }
    }
}
