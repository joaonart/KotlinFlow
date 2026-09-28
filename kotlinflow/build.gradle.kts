import com.vanniktech.maven.publish.SonatypeHost
import java.util.Base64

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

// Resolve and normalize GPG signing credentials for reliable CI/CD signing
val rawSigningKey = (project.findProperty("signingInMemoryKey") as String?)
    ?: System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey")
    ?: System.getenv("GPG_SIGNING_KEY")

val normalizedSigningKey: String? = rawSigningKey?.let { keyStr ->
    val trimmed = keyStr.trim()
    if (trimmed.startsWith("-----BEGIN")) {
        trimmed.replace("\r\n", "\n").replace("\\n", "\n")
    } else {
        try {
            val decoded = String(Base64.getMimeDecoder().decode(trimmed), Charsets.UTF_8)
            decoded.replace("\r\n", "\n").replace("\\n", "\n")
        } catch (_: Exception) {
            trimmed.replace("\r\n", "\n").replace("\\n", "\n")
        }
    }
}

if (!normalizedSigningKey.isNullOrBlank()) {
    project.extra.set("signingInMemoryKey", normalizedSigningKey)
}

val rawKeyId = (project.findProperty("signingInMemoryKeyId") as String?)
    ?: System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKeyId")
    ?: System.getenv("GPG_KEY_ID")

val normalizedKeyId: String? = rawKeyId?.let { idStr ->
    val clean = idStr.trim().removePrefix("0x").removePrefix("0X")
    if (clean.length > 8) clean.takeLast(8) else clean
}

if (!normalizedKeyId.isNullOrBlank()) {
    project.extra.set("signingInMemoryKeyId", normalizedKeyId)
}

val rawSigningPassword = (project.findProperty("signingInMemoryKeyPassword") as String?)
    ?: System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKeyPassword")
    ?: System.getenv("GPG_SIGNING_PASSPHRASE")

if (rawSigningPassword != null) {
    project.extra.set("signingInMemoryKeyPassword", rawSigningPassword.trimEnd('\r', '\n'))
}

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
