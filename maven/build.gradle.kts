dependencies {
    api("org.ow2.asm:asm:9.2")
    api("org.ow2.asm:asm-commons:9.2")
    api(project(":common")) {
        exclude("org.reflections", "reflections")
        exclude("net.blueberrymc", "native-util")
        exclude("com.google.guava", "guava")
    }
}
