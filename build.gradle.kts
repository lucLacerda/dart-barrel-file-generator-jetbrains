import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "com.dbfg"
version = "1.0.2"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create("IC", "2025.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        // Add necessary plugin dependencies for compilation here, example:
        // bundledPlugin("com.intellij.java")
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellijPlatform {
    pluginVerification {
//        ides {
//            localPaths.add("/Applications/IntelliJ IDEA.app")
//            versions.add("2023.3")
//            versions.add("2024.1")
//            versions.add("2025.1")
//        }
    }
    
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "233"
            untilBuild = "251.*"
        }

        // O pluginId é definido no plugin.xml
        name = "Dart Barrel Generator"
        description = """Plugin to generate barrel files in Dart and Flutter projects, simplifying the organization and import of multiple files.""".trimIndent()

        changeNotes = """
        <h2>1.0.0</h2>
        <ul>
            <li>Initial plugin version</li>
            <li>Support for generating barrel files in folders</li>
            <li>Support for recursive generation of barrel files</li>
            <li>Support for generating barrel files with subfolders</li>
            <li>Customizable settings for file name, exclusions, and export format</li>
            <li>Detailed statistics about processed files</li>
        </ul>
        """.trimIndent()
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    
    buildPlugin {
        archiveBaseName.set("dart-barrel-generator")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
