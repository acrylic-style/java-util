plugins {
    java
    `maven-publish`
    `java-library`
}

group = "xyz.acrylicstyle.java-util"
version = "1.2.1-SNAPSHOT"

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
        toolchain.languageVersion.set(JavaLanguageVersion.of(8))
        //withJavadocJar()
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
                        project.findProperty("deploySnapshotURL") ?: System.getProperty("deploySnapshotURL", "https://repo.acrylicstyle.xyz/repository/maven-snapshots/")
                    else
                        project.findProperty("deployReleasesURL") ?: System.getProperty("deployReleasesURL", "https://repo.acrylicstyle.xyz/repository/maven-releases/")
                )
            }
            maven {
                name = "azisaba"
                credentials(PasswordCredentials::class)
                url = uri(
                    if (project.version.toString().endsWith("SNAPSHOT"))
                        project.findProperty("azisabaDeploySnapshotURL")
                            ?: System.getProperty("azisabaDeploySnapshotURL", "https://repo.azisaba.net/repository/third-party/")
                    else
                        project.findProperty("azisabaDeployReleasesURL")
                            ?: System.getProperty("azisabaDeployReleasesURL", "https://repo.azisaba.net/repository/third-party/")
                )
            }
        }

        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
            }
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

        javadoc {
            options.source = "8"
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
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}
