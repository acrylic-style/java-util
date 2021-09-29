plugins {
    java
    kotlin("jvm") version "1.5.31"
    `maven-publish`
}

group = "xyz.acrylicstyle.util"
version = "0.16.1"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://repo2.acrylicstyle.xyz") }
}

subprojects {
    group = parent!!.group
    version = parent!!.version

    repositories {
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://repo2.acrylicstyle.xyz") }
    }

    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("maven-publish")
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
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/acrylic-style/java-util")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME") ?: "acrylic-style"
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
                }
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
        if (name != "common") implementation(project(":common"))
        implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
        implementation("org.jetbrains:annotations:22.0.0")
        implementation("org.json:json:20210307")
        implementation("net.blueberrymc:native-util:1.2.4")
        implementation("com.google.guava:guava:30.1.1-jre")
        compileOnly("org.projectlombok:lombok:1.18.20")
        annotationProcessor("org.projectlombok:lombok:1.18.20")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.5.31")
        testImplementation("org.junit.jupiter:junit-jupiter:5.8.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    }
}
