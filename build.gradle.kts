plugins {
    id("java")
    `maven-publish`
    `java-library`
}

group = "xyz.acrylicstyle"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    group = parent!!.group
    version = parent!!.version

    repositories {
        // mavenLocal()
        mavenCentral()
    }

    apply {
        plugin("java")
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
        test {
            useJUnitPlatform()
        }

        processResources {
            from(sourceSets.main.get().resources.srcDirs) {
                include("**")
                val tokenReplacementMap = mapOf("version" to project.version)
                filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to tokenReplacementMap)
            }
            filteringCharset = "UTF-8"
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            from(projectDir) { include("LICENSE") }
        }

        compileJava {
            options.encoding = "UTF-8"
            options.isDeprecation = true
            // options.compilerArgs.add("-Xlint:unchecked")
        }
    }
}

subprojects {
    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}
