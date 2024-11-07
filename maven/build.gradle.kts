java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

dependencies {
    val mavenResolverVersion = "2.0.0"
    compileOnly("org.jetbrains:annotations:26.0.1")
    api("org.apache.maven.resolver:maven-resolver-api:$mavenResolverVersion")
    api("org.apache.maven.resolver:maven-resolver-spi:$mavenResolverVersion")
    api("org.apache.maven.resolver:maven-resolver-util:$mavenResolverVersion")
    api("org.apache.maven.resolver:maven-resolver-impl:$mavenResolverVersion")
    api("org.apache.maven.resolver:maven-resolver-connector-basic:$mavenResolverVersion")
    api("org.apache.maven.resolver:maven-resolver-transport-file:$mavenResolverVersion")
    api("org.apache.maven.resolver:maven-resolver-transport-jdk:$mavenResolverVersion")
    api("org.apache.maven.resolver:maven-resolver-transport-apache:$mavenResolverVersion")
    api("org.apache.maven.resolver:maven-resolver-supplier-mvn4:$mavenResolverVersion")
    api("org.apache.maven.resolver:maven-resolver-generator-gnupg:$mavenResolverVersion")
    api("org.apache.maven:maven-resolver-provider:3.9.9")
}
