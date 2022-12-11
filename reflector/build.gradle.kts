repositories {
    maven { url = uri("https://repo.blueberrymc.net/repository/maven-public/") }
}

dependencies {
    api(project(":common"))
    compileOnly("net.blueberrymc:native-util:2.1.0")
}
