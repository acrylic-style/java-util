repositories {
    maven { url = uri("https://repo.blueberrymc.net/repository/maven-public/") }
}

dependencies {
    //api(project(":common"))
    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("net.blueberrymc:native-util:2.1.0")
    testCompileOnly("org.jetbrains:annotations:23.0.0")
}
