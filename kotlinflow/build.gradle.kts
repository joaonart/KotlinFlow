import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vanniktech.maven.publish)
}

android {
    namespace = "io.github.kotlinflow"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
        )
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui)
}

val releaseVersion = project.findProperty("VERSION_NAME") as String?
    ?: System.getenv("VERSION_NAME")?.removePrefix("v")
    ?: "1.0.0"

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    if (project.hasProperty("signingInMemoryKey") ||
        System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey") != null ||
        project.hasProperty("signing.keyId")
    ) {
        signAllPublications()
    }
    coordinates(
        groupId = "io.github.joaonart",
        artifactId = "kotlinflow",
        version = releaseVersion
    )

    pom {
        name.set("KotlinFlow")
        description.set("A faithful, highly optimized Android port of SwiftFlow built natively with Jetpack Compose.")
        inceptionYear.set("2026")
        url.set("https://github.com/joaonart/KotlinFlow")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("joaonart")
                name.set("João Alves")
                email.set("joao.alves64@gmail.com")
                url.set("https://github.com/joaonart")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/joaonart/KotlinFlow.git")
            developerConnection.set("scm:git:ssh://github.com:joaonart/KotlinFlow.git")
            url.set("https://github.com/joaonart/KotlinFlow")
        }
    }
}
