// TODO: agent should be included in the -all jar, and remove the :all dependency
dependencies {
    api(project(":all"))
    api(project(":reflector"))
    api("org.javassist:javassist:3.28.0-GA")
}
