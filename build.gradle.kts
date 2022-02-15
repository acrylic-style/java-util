plugins {
    java
    kotlin("jvm") version "1.6.0"
    `maven-publish`
    `java-library`
}

group = "xyz.acrylicstyle.util"
version = "0.16.6"

repositories {
    // mavenLocal()
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://repo.blueberrymc.net/repository/maven-public/") }
}

subprojects {
    group = parent!!.group
    version = parent!!.version

    repositories {
        // mavenLocal()
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://repo.blueberrymc.net/repository/maven-public/") }
    }

    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("maven-publish")
        plugin("java-library")
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    val javaComponent = components["java"] as AdhocComponentWithVariants
    javaComponent.withVariantsFromConfiguration(configurations["sourcesElements"]) {
        skip()
    }

    publishing {
        repositories {
            maven {
                name = "repo"
                credentials(PasswordCredentials::class)
                url = uri(
                    if (project.version.toString().endsWith("SNAPSHOT"))
                        project.findProperty("deploySnapshotURL") ?: System.getProperty("deploySnapshotURL", "")
                    else
                        project.findProperty("deployReleasesURL") ?: System.getProperty("deployReleasesURL", "")
                )
            }
        }

        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
            }
        }
    }
}

subprojects {
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of("8"))
        }
    }

    tasks {
        compileKotlin { kotlinOptions.jvmTarget = "1.8" }
        compileTestKotlin { kotlinOptions.jvmTarget = "1.8" }

        test {
            useJUnitPlatform()
        }

        withType<ProcessResources> {
            from(sourceSets.main.get().resources.srcDirs) {
                include("**")
                val tokenReplacementMap = mapOf("version" to project.version)
                filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to tokenReplacementMap)
            }
            filteringCharset = "UTF-8"
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            from(projectDir) { include("LICENSE") }
        }

        withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            options.isDeprecation = true
            // options.compilerArgs.add("-Xlint:unchecked")
        }
    }
}

subprojects {
    dependencies {
        if (name != "common" && name != "maven") api(project(":common"))
        compileOnly("org.jetbrains:annotations:22.0.0")
        compileOnly("org.projectlombok:lombok:1.18.20")
        annotationProcessor("org.projectlombok:lombok:1.18.20")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.5.31")
        testImplementation("org.junit.jupiter:junit-jupiter:5.8.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    }
}
