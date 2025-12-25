plugins {
    java
    `maven-publish`
    `java-library`
    signing
}

group = "xyz.acrylicstyle.java-util"
version = "2.1.1"
description = "Provides the (probably) useful methods for Java."

repositories {
    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    withJavadocJar()
    withSourcesJar()
}

subprojects {
    group = parent!!.group
    version = parent!!.version
    description = "Provides the (probably) useful methods for Java. (this is placeholder)"

    repositories {
        // mavenLocal()
        mavenCentral()
    }

    apply {
        plugin("java")
        plugin("maven-publish")
        plugin("java-library")
        plugin("signing")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(8))
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

                pom {
                    description = project.description
                    url = "https://github.com/acrylic-style/java-util"

                    scm {
                        connection = "scm:git:git://github.com/acrylic-style/java-util.git"
                        developerConnection = "scm:git:ssh://github.com:acrylic-style/java-util.git"
                        url = "https://github.com/acrylic-style/java-util"
                    }

                    licenses {
                        license {
                            name = "MIT License"
                            url = "https://github.com/acrylic-style/java-util/blob/main/LICENSE"
                        }
                    }

                    developers {
                        developer {
                            name = "acrylic-style"
                            email = "me@acrylicstyle.xyz"
                        }
                    }
                }
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

allprojects {
    signing {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    register("publishToCentral") {
        if (project.version.toString().endsWith("SNAPSHOT")) {
            dependsOn("publishAggregationToCentralPortalSnapshots")
        } else {
            dependsOn("publishAggregationToCentralPortal")
        }
    }
}
